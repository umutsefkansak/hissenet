package com.infina.hissenet.service;

import com.infina.hissenet.constants.MailConstants;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.entity.VerificationCode;
import com.infina.hissenet.entity.enums.EmailType;
import com.infina.hissenet.exception.mail.MailException;
import com.infina.hissenet.exception.mail.MailRateLimitException;
import com.infina.hissenet.exception.mail.VerificationCodeException;
import com.infina.hissenet.exception.mail.VerificationCodeNotFoundException;
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
    private int codeLength;

    @Value("${mail.verification.expiry-minutes:10}")
    private int expiryMinutes;

    @Value("${mail.verification.max-attempts:3}")
    private int defaultMaxAttempts;

    @Value("${mail.verification.ip-limit-per-hour:20}")
    private int ipLimitPerHour;

    private static final SecureRandom random = new SecureRandom();

    public MailService(JavaMailSender mailSender, VerificationCodeRepository verificationCodeRepository, IEmailTemplateService emailTemplateService) {
        this.mailSender = mailSender;
        this.verificationCodeRepository = verificationCodeRepository;
        this.emailTemplateService = emailTemplateService;
    }


    @Async
    public MailSendResponse sendMail(MailSendDto request) {
        try {
            MimeMessage message = createMimeMessage(request.to(), request.subject(),
                    request.content(), request.recipientName());
            mailSender.send(message);

            logger.info("Email sent successfully: {} -> {}", fromEmail, request.to());
            return MailSendResponse.success(MailConstants.Messages.MAIL_SENT_SUCCESS);

        } catch (Exception e) {
            logger.error("Error sending email: {} -> {}", fromEmail, request.to(), e);
            throw new MailException(MailConstants.Messages.MAIL_SEND_ERROR + e.getMessage(), e);
        }
    }


    public MailSendResponse sendVerificationCode(VerificationCodeSendDto request) {
        return sendVerificationCodeWithAttempts(request, defaultMaxAttempts);
    }


    public MailSendResponse sendVerificationCodeWithAttempts(VerificationCodeSendDto request, int maxAttempts) {
        // Check daily request limit
        long todayCount = verificationCodeRepository.countCodesSentSince(
                request.to(), LocalDateTime.now().minusDays(1));

        if (todayCount >= MailConstants.MAX_CODES_PER_DAY) {
            throw new MailRateLimitException(MailConstants.Messages.DAILY_LIMIT_EXCEEDED);
        }

        // Check if the code is blocked
        if (verificationCodeRepository.hasBlockedCodeForEmailAndType(request.to(), request.type(), LocalDateTime.now())) {
            throw new VerificationCodeException(MailConstants.Messages.TOO_MANY_WRONG_ATTEMPTS);
        }

        // Invalidate old codes
        verificationCodeRepository.invalidateAllCodesForEmailAndType(request.to(), request.type());

        // Generate new code
        String code = generateVerificationCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expiryMinutes);

        // Save to database
        VerificationCode verificationCode = new VerificationCode(request.to(), code,
                request.type(), expiresAt, maxAttempts);
        verificationCodeRepository.save(verificationCode);

        // Create email content
        String subject = createSubjectForType(request.type());
        String content = createContentForVerificationCode(code, request.recipientName(),
                request.type(), request.additionalInfo(), maxAttempts);

        // Send email
        try {
            MimeMessage message = createMimeMessage(request.to(), subject, content, request.recipientName());
            mailSender.send(message);

            logger.info("Verification code sent: {} -> {} (Type: {}, MaxAttempts: {})",
                    fromEmail, request.to(), request.type(), maxAttempts);
            return MailSendResponse.successWithCode(MailConstants.Messages.VERIFICATION_CODE_SENT,
                    code, maxAttempts, expiryMinutes);

        } catch (Exception e) {
            logger.error("Error sending verification code: {} -> {}", fromEmail, request.to(), e);
            throw new MailException(MailConstants.Messages.VERIFICATION_CODE_SEND_ERROR + e.getMessage(), e);
        }
    }


    public VerifyCodeResponse verifyCode(VerifyCodeDto dto, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);

        // Check IP-based attempt limit
        long ipAttempts = verificationCodeRepository.countAttemptsByIpSince(
                ipAddress, LocalDateTime.now().minusHours(1));

        if (ipAttempts >= ipLimitPerHour) {
            logger.warn("IP-based attempt limit exceeded: {} (Attempts: {})", ipAddress, ipAttempts);
            throw new MailRateLimitException(MailConstants.Messages.IP_LIMIT_EXCEEDED);
        }

        // Find active verification code
        Optional<VerificationCode> activeCodeOpt = verificationCodeRepository
                .findActiveCodeByEmailAndType(dto.email(), dto.type(), LocalDateTime.now());

        if (activeCodeOpt.isEmpty()) {
            logger.warn("No active verification code found: {} (Type: {})", dto.email(), dto.type());
            throw new VerificationCodeNotFoundException(MailConstants.Messages.ACTIVE_CODE_NOT_FOUND);
        }

        VerificationCode activeCode = activeCodeOpt.get();

        // Verify if the code is correct
        if (!activeCode.getCode().equals(dto.code())) {
            // Incorrect code - increase attempt count
            incrementVerificationAttempt(activeCode);
            activeCode.setIpAddress(ipAddress);
            verificationCodeRepository.save(activeCode);

            int remainingAttempts = getRemainingAttempts(activeCode);

            if (activeCode.getBlocked()) {
                logger.warn("Code blocked due to too many failed attempts: {} (Type: {})", dto.email(), dto.type());
                throw new VerificationCodeException(MailConstants.Messages.CODE_BLOCKED);

            } else {
                logger.warn("Incorrect verification code: {} (Type: {}, Remaining: {})",
                        dto.email(), dto.type(), remainingAttempts);
                throw new VerificationCodeException(
                        String.format(MailConstants.Messages.WRONG_CODE_FORMAT, remainingAttempts)
                );
            }
        }

        // Correct code - mark as used
        markAsUsed(activeCode, ipAddress);
        verificationCodeRepository.save(activeCode);

        logger.info("Verification code successfully validated: {} (Type: {})", dto.email(), dto.type());
        return VerifyCodeResponse.success(MailConstants.Messages.CODE_VERIFIED_SUCCESS);
    }


    public MailSendResponse sendHighSecurityCode(VerificationCodeSendDto request) {
        return sendVerificationCodeWithAttempts(request, MailConstants.HIGH_SECURITY_MAX_ATTEMPTS);
    }


    public MailSendResponse sendStandardCode(VerificationCodeSendDto request) {
        return sendVerificationCodeWithAttempts(request, MailConstants.STANDARD_SECURITY_MAX_ATTEMPTS);
    }


    public MailSendResponse sendLowSecurityCode(VerificationCodeSendDto request) {
        return sendVerificationCodeWithAttempts(request, MailConstants.LOW_SECURITY_MAX_ATTEMPTS);
    }

    public void markAsUsed(VerificationCode code, String ipAddress) {
        code.setUsed(true);
        code.setUsedAt(LocalDateTime.now());
        code.setIpAddress(ipAddress);
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


    @Async
    public void sendNotification(String email, String recipientName, EmailType type, String message) {
        String subject = createSubjectForType(type);
        String content = createNotificationContent(recipientName, type, message);

        MailSendDto request = new MailSendDto(email, type, subject, content, recipientName);
        sendMail(request);
    }


    public MailSendResponse sendLoginCode(LoginCodeDto dto) {
        VerificationCodeSendDto verificationDto = new VerificationCodeSendDto(
                dto.email(),
                EmailType.LOGIN_NOTIFICATION,
                dto.recipientName(),
                MailConstants.Messages.LOGIN_CODE_ADDITIONAL_INFO
        );
        return sendStandardCode(verificationDto);
    }


    public MailSendResponse sendPasswordResetCode(PasswordResetCodeDto dto) {
        VerificationCodeSendDto verificationDto = new VerificationCodeSendDto(
                dto.email(),
                EmailType.PASSWORD_RESET,
                dto.recipientName(),
                MailConstants.Messages.PASSWORD_RESET_ADDITIONAL_INFO
        );
        return sendHighSecurityCode(verificationDto);
    }


    private MimeMessage createMimeMessage(String to, String subject, String content, String recipientName)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

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
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private String createSubjectForType(EmailType type) {
        return switch (type) {
            case VERIFICATION_CODE -> MailConstants.Subjects.VERIFICATION_CODE;
            case PASSWORD_RESET -> MailConstants.Subjects.PASSWORD_RESET;
            case LOGIN_NOTIFICATION -> MailConstants.Subjects.LOGIN_NOTIFICATION;
            case TRADE_NOTIFICATION -> MailConstants.Subjects.TRADE_NOTIFICATION;
            case ACCOUNT_STATUS -> MailConstants.Subjects.ACCOUNT_STATUS;
            case ERROR_NOTIFICATION -> MailConstants.Subjects.ERROR_NOTIFICATION;
            default -> MailConstants.Subjects.DEFAULT;
        };
    }

    private String createContentForVerificationCode(String code, String recipientName,
                                                    EmailType type, String additionalInfo, int maxAttempts) {
        return emailTemplateService.renderVerificationCodeTemplate(
                recipientName, code, type, additionalInfo, maxAttempts, expiryMinutes
        );
    }
    private String createNotificationContent(String recipientName, EmailType type, String message) {
        return emailTemplateService.renderNotificationTemplate(recipientName, type, message);
    }


    public void cleanupExpiredCodes() {
        verificationCodeRepository.deleteExpiredCodes(
                LocalDateTime.now().minusDays(MailConstants.EXPIRED_CODES_CLEANUP_DAYS));
        logger.info(MailConstants.Messages.EXPIRED_CODES_CLEANED);
    }


    public void unblockExpiredCodes() {
        verificationCodeRepository.unblockOldCodes(
                LocalDateTime.now().minusHours(MailConstants.BLOCKED_CODES_UNBLOCK_HOURS));
        logger.info(MailConstants.Messages.BLOCKED_CODES_UNBLOCKED);
    }


    public boolean isEmailLimitExceeded(String email) {
        long todayCount = verificationCodeRepository.countCodesSentSince(
                email, LocalDateTime.now().minusDays(1));
        return todayCount >= MailConstants.MAX_CODES_PER_DAY;
    }


    public boolean isIpLimitExceeded(String ipAddress) {
        long hourlyCount = verificationCodeRepository.countAttemptsByIpSince(
                ipAddress, LocalDateTime.now().minusHours(1));
        return hourlyCount >= ipLimitPerHour;
    }

    public boolean isCodeExpired(VerificationCode code) {
        return LocalDateTime.now().isAfter(code.getExpiresAt());
    }

    public boolean isCodeValid(VerificationCode code) {
        return !code.getUsed() && !isCodeExpired(code) && !code.getBlocked();
    }

    public boolean canAttemptVerification(VerificationCode code) {
        return !code.getBlocked() &&
                code.getAttemptCount() < code.getMaxAttempts() &&
                !isCodeExpired(code) &&
                !code.getUsed();
    }

    public void incrementVerificationAttempt(VerificationCode code) {
        code.setAttemptCount(code.getAttemptCount() + 1);
        code.setLastAttemptAt(LocalDateTime.now());

        if (code.getAttemptCount() >= code.getMaxAttempts()) {
            code.setBlocked(true);
            code.setBlockedAt(LocalDateTime.now());
        }
    }

    public int getRemainingAttempts(VerificationCode code) {
        return Math.max(0, code.getMaxAttempts() - code.getAttemptCount());
    }

}