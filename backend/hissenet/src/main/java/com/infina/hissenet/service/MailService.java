package com.infina.hissenet.service;

import com.infina.hissenet.constants.MailConstants;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.entity.VerificationCode;
import com.infina.hissenet.exception.mail.*;
import com.infina.hissenet.repository.VerificationCodeRepository;
import com.infina.hissenet.service.abstracts.IEmailTemplateService;
import com.infina.hissenet.service.abstracts.IMailService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class MailService implements IMailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;
    private final IEmailTemplateService emailTemplateService;

    @Value("${mail.from.email}")
    private String fromEmail;

    @Value("${mail.from.name}")
    private String fromName;

    @Value("${mail.verification.code-length:6}")
    private int defaultCodeLength;

    @Value("${mail.verification.default-expiry-minutes:10}")
    private int defaultExpiryMinutes;

    @Value("${mail.verification.default-max-attempts:3}")
    private int defaultMaxAttempts;

    @Value("${mail.verification.ip-limit-per-hour:20}")
    private int ipLimitPerHour;

    @Value("${mail.verification.max-codes-per-day:10}")
    private int maxCodesPerDay;

    private static final SecureRandom random = new SecureRandom();

    public MailService(JavaMailSender mailSender, VerificationCodeRepository verificationCodeRepository,
                       IEmailTemplateService emailTemplateService) {
        this.mailSender = mailSender;
        this.verificationCodeRepository = verificationCodeRepository;
        this.emailTemplateService = emailTemplateService;
    }

    @Async
    public MailSendResponse sendMail(MailSendRequest request) {
        try {
            MimeMessage message = createMimeMessage(
                    request.to(),
                    request.subject(),
                    request.content(),
                    request.recipientName()
            );
            mailSender.send(message);

            logger.info("Email sent successfully: {} -> {}", fromEmail, request.to());
            return MailSendResponse.success(MailConstants.Messages.MAIL_SENT_SUCCESS);

        } catch (Exception e) {
            logger.error("Error sending email: {} -> {}", fromEmail, request.to(), e);
            throw new MailException(MailConstants.Messages.MAIL_SEND_ERROR + e.getMessage(), e);
        }
    }

    public CodeSendResponse sendVerificationCode(CodeSendRequest request) {

        int maxAttempts = request.maxAttempts() != null ? request.maxAttempts() : defaultMaxAttempts;
        int expiryMinutes = request.expiryMinutes() != null ? request.expiryMinutes() : defaultExpiryMinutes;


        long todayCount = verificationCodeRepository.countCodesSentSince(
                request.email(), LocalDateTime.now().minusDays(1));

        if (todayCount >= maxCodesPerDay) {
            throw new MailRateLimitException(MailConstants.Messages.DAILY_LIMIT_EXCEEDED);
        }


        long blockedCount = verificationCodeRepository.countBlockedCodesByEmail(
                request.email(), LocalDateTime.now().minusHours(1));

        if (blockedCount > 0) {
            throw new VerificationCodeException(MailConstants.Messages.TOO_MANY_WRONG_ATTEMPTS);
        }


        String code = generateVerificationCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expiryMinutes);


        String subject = createVerificationSubject(request.description());
        String content = createVerificationContent(
                request.recipientName(),
                code,
                request.description(),
                request.additionalInfo(),
                maxAttempts,
                expiryMinutes
        );

        try {
            MimeMessage message = createMimeMessage(request.email(), subject, content, request.recipientName());
            mailSender.send(message);


            verificationCodeRepository.invalidateAllCodesForEmail(request.email());


            VerificationCode verificationCode = new VerificationCode(
                    request.email(), code, request.description(), expiresAt, maxAttempts
            );
            verificationCodeRepository.save(verificationCode);

            logger.info("Verification code sent: {} -> {} (MaxAttempts: {}, ExpiryMinutes: {})",
                    fromEmail, request.email(), maxAttempts, expiryMinutes);

            return CodeSendResponse.success(
                    MailConstants.Messages.VERIFICATION_CODE_SENT,
                    maxAttempts,
                    expiryMinutes
            );

        } catch (Exception e) {
            logger.error("Error sending verification code: {} -> {}", fromEmail, request.email(), e);
            throw new MailException(MailConstants.Messages.VERIFICATION_CODE_SEND_ERROR + e.getMessage(), e);
        }
    }

    public CodeVerifyResponse verifyCode(CodeVerifyRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);


        long ipAttempts = verificationCodeRepository.countAttemptsByIpSince(
                ipAddress, LocalDateTime.now().minusHours(1));

        if (ipAttempts >= ipLimitPerHour) {
            logger.warn("IP-based attempt limit exceeded: {} (Attempts: {})", ipAddress, ipAttempts);
            return CodeVerifyResponse.failure(MailConstants.Messages.IP_LIMIT_EXCEEDED, 0);
        }


        Optional<VerificationCode> activeCodeOpt = verificationCodeRepository
                .findActiveCodeByEmail(request.email(), LocalDateTime.now());

        if (activeCodeOpt.isEmpty()) {
            logger.warn("No active verification code found: {}", request.email());
            return CodeVerifyResponse.failure(MailConstants.Messages.ACTIVE_CODE_NOT_FOUND, 0);
        }

        VerificationCode activeCode = activeCodeOpt.get();


        if (!activeCode.getCode().equals(request.code())) {

            incrementAttempt(activeCode, ipAddress);
            verificationCodeRepository.save(activeCode);

            int remainingAttempts = getRemainingAttempts(activeCode);

            if (activeCode.getBlocked()) {
                logger.warn("Code blocked due to too many failed attempts: {}", request.email());
                return CodeVerifyResponse.blocked(
                        MailConstants.Messages.CODE_BLOCKED,
                        activeCode.getBlockedAt()
                );
            } else {
                logger.warn("Incorrect verification code: {} (Remaining: {})",
                        request.email(), remainingAttempts);
                return CodeVerifyResponse.failure(
                        String.format(MailConstants.Messages.WRONG_CODE_FORMAT, remainingAttempts),
                        remainingAttempts
                );
            }
        }


        markAsUsed(activeCode, ipAddress);
        verificationCodeRepository.save(activeCode);

        logger.info("Verification code successfully validated: {}", request.email());
        return CodeVerifyResponse.success(MailConstants.Messages.CODE_VERIFIED_SUCCESS);
    }

    @Async
    public void sendNotification(NotificationSendRequest request) {
        String subject = request.title() != null ? request.title() : MailConstants.Config.DEFAULT_NOTIFICATION_SUBJECT;;
        String content = createNotificationContent(
                request.recipientName(),
                request.message()
        );

        MailSendRequest mailRequest = new MailSendRequest(
                request.email(),
                subject,
                content,
                request.recipientName()
        );

        sendMail(mailRequest);
    }

    private MimeMessage createMimeMessage(String to, String subject, String content, String recipientName)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, MailConstants.Config.DEFAULT_MAIL_ENCODING);

        try {
            helper.setFrom(fromEmail, fromName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        return message;
    }

    private String generateVerificationCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < defaultCodeLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private String createVerificationSubject(String description) {
        if (description != null && !description.trim().isEmpty()) {
            return String.format(MailConstants.Config.VERIFICATION_SUBJECT_WITH_DESC_FORMAT, description);
        }
        return MailConstants.Config.DEFAULT_VERIFICATION_SUBJECT;
    }

    private String createVerificationContent(String recipientName, String code, String description,
                                             String additionalInfo, int maxAttempts, int expiryMinutes) {
        return emailTemplateService.renderVerificationTemplate(
                recipientName, code, description, additionalInfo, maxAttempts, expiryMinutes
        );
    }

    private String createNotificationContent(String recipientName, String message) {
        return emailTemplateService.renderNotificationTemplate(recipientName, message);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(MailConstants.HttpHeaders.X_FORWARDED_FOR);
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader(MailConstants.HttpHeaders.X_REAL_IP);
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private void incrementAttempt(VerificationCode code, String ipAddress) {
        code.setAttemptCount(code.getAttemptCount() + 1);
        code.setLastAttemptAt(LocalDateTime.now());
        code.setIpAddress(ipAddress);

        if (code.getAttemptCount() >= code.getMaxAttempts()) {
            code.setBlocked(true);
            code.setBlockedAt(LocalDateTime.now());
        }
    }

    private void markAsUsed(VerificationCode code, String ipAddress) {
        code.setUsed(true);
        code.setUsedAt(LocalDateTime.now());
        code.setIpAddress(ipAddress);
    }

    private int getRemainingAttempts(VerificationCode code) {
        return Math.max(0, code.getMaxAttempts() - code.getAttemptCount());
    }


    public void cleanupExpiredCodes() {
        verificationCodeRepository.deleteExpiredCodes(LocalDateTime.now().minusDays(MailConstants.EXPIRED_CODES_CLEANUP_DAYS));
        logger.info(MailConstants.Messages.EXPIRED_CODES_CLEANED);
    }

    public void unblockExpiredCodes() {
        verificationCodeRepository.unblockOldCodes(LocalDateTime.now().minusHours(MailConstants.BLOCKED_CODES_UNBLOCK_HOURS));
        logger.info(MailConstants.Messages.BLOCKED_CODES_UNBLOCKED);
    }

    public boolean isEmailLimitExceeded(String email) {
        long todayCount = verificationCodeRepository.countCodesSentSince(
                email, LocalDateTime.now().minusDays(1));
        return todayCount >= MailConstants.MAX_CODES_PER_DAY;
    }

}