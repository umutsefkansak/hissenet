package com.infina.hissenet.service;

import com.infina.hissenet.constants.MailConstants;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.exception.mail.MailException;
import com.infina.hissenet.repository.EmployeeRepository;
import com.infina.hissenet.service.abstracts.IEmailTemplateService;
import com.infina.hissenet.service.abstracts.IEmployeeService;
import com.infina.hissenet.service.abstracts.IMailService;
import com.infina.hissenet.service.abstracts.IVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

@Service
public class MailService implements IMailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final IEmployeeService employeeService;


    private final JavaMailSender mailSender;
    private final IEmailTemplateService emailTemplateService;
    private final IVerificationService verificationService;

    @Value("${mail.from.email}")
    private String fromEmail;

    @Value("${mail.from.name}")
    private String fromName;

    @Value("${mail.verification.default-expiry-minutes:10}")
    private int defaultExpiryMinutes;

    @Value("${mail.verification.default-max-attempts:3}")
    private int defaultMaxAttempts;

    @Value("${mail.company.name}")
    private String companyName;

    public MailService(IEmployeeService employeeService, JavaMailSender mailSender,
                       IEmailTemplateService emailTemplateService,
                       IVerificationService verificationService) {
        this.employeeService = employeeService;
        this.mailSender = mailSender;
        this.emailTemplateService = emailTemplateService;
        this.verificationService = verificationService;
    }

    @Async
    @Override
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

    @Override
    public CodeSendResponse sendVerificationCode(CodeSendRequest request) {
        int maxAttempts = request.maxAttempts() != null ? request.maxAttempts() : defaultMaxAttempts;
        int expiryMinutes = request.expiryMinutes() != null ? request.expiryMinutes() : defaultExpiryMinutes;

        try {

            String code = verificationService.generateAndStoreCode(request);

            String subject = createVerificationSubject(request.description());
            String content = createVerificationContent(
                    request.recipientName(),
                    code,
                    request.description(),
                    request.additionalInfo(),
                    maxAttempts,
                    expiryMinutes
            );

            MimeMessage message = createMimeMessage(request.email(), subject, content, request.recipientName());
            mailSender.send(message);

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

    /*@Override
    public CodeSendResponse sendPasswordResetCode(CodeSendRequest request) {
        int maxAttempts = request.maxAttempts() != null ? request.maxAttempts() : defaultMaxAttempts;
        int expiryMinutes = request.expiryMinutes() != null ? request.expiryMinutes() : defaultExpiryMinutes;

        if(employeeService.existsByEmail(request.email())){
            return sendVerificationCode(request);
        }
        return CodeSendResponse.success(
                MailConstants.Messages.VERIFICATION_CODE_SENT,
                maxAttempts,
                expiryMinutes
        );
    }

    */

    @Override
    public CodeSendResponse sendPasswordResetCode(CodeSendRequest request) {
        int maxAttempts = request.maxAttempts() != null ? request.maxAttempts() : defaultMaxAttempts;
        int expiryMinutes = request.expiryMinutes() != null ? request.expiryMinutes() : defaultExpiryMinutes;

        // Async processing was used to prevent timing attacks - consistent response time regardless of email existence
        CompletableFuture.runAsync(() -> {
            if (employeeService.existsByEmail(request.email())) {
                try {
                    sendVerificationCode(request);
                } catch (Exception e) {
                    logger.error("Error sending password reset code to: {}", request.email(), e);
                }
            }
        });

        return CodeSendResponse.success(
                MailConstants.Messages.VERIFICATION_CODE_SENT,
                maxAttempts,
                expiryMinutes
        );
    }

    @Override
    public CodeVerifyResponse verifyCode(CodeVerifyRequest request, HttpServletRequest httpRequest) {
        return verificationService.verifyCode(request, httpRequest);
    }

    @Async
    @Override
    public void sendNotification(NotificationSendRequest request) {
        String subject = request.title() != null ? request.title() : MailConstants.Config.DEFAULT_NOTIFICATION_SUBJECT;
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

    @Override
    public boolean isEmailLimitExceeded(String email) {
        return verificationService.isEmailLimitExceeded(email);
    }





    private MimeMessage createMimeMessage(String to, String subject, String content, String recipientName)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, MailConstants.Config.DEFAULT_MAIL_ENCODING);

        try {
            helper.setFrom(fromEmail, fromName);
        } catch (UnsupportedEncodingException e) {
            throw new MailException("Error setting email sender: " + e.getMessage(), e);
        }

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        return message;
    }

    private String createVerificationSubject(String description) {
        if (description != null && !description.trim().isEmpty()) {
            return String.format(MailConstants.Config.VERIFICATION_SUBJECT_WITH_DESC_FORMAT, description);
        }
        return String.format(MailConstants.Subjects.VERIFICATION_CODE_FORMAT, companyName);
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

}