package com.infina.hissenet.service;

import com.infina.hissenet.constants.MailConstants;
import com.infina.hissenet.dto.common.CorporateCustomerDto;
import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.dto.common.IndividualCustomerDto;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.CodeSendResponse;
import com.infina.hissenet.dto.response.CodeVerifyResponse;
import com.infina.hissenet.dto.response.MailSendResponse;
import com.infina.hissenet.dto.response.PasswordChangeTokenResponse;
import com.infina.hissenet.entity.CorporateCustomer;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.IndividualCustomer;
import com.infina.hissenet.exception.mail.MailException;
import com.infina.hissenet.service.abstracts.*;
import com.infina.hissenet.utils.MessageUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class MailService implements IMailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final IEmailTemplateService emailTemplateService;
    private final IVerificationService verificationService;
    private final ICustomerService customerService;


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

    public MailService(JavaMailSender mailSender,
                       IEmailTemplateService emailTemplateService,
                       IVerificationService verificationService, ICustomerService customerService) {
        this.mailSender = mailSender;
        this.emailTemplateService = emailTemplateService;
        this.verificationService = verificationService;

        this.customerService = customerService;
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
            return MailSendResponse.success(MessageUtils.getMessage("mail.send.success"));
        } catch (Exception e) {
            logger.error("Error sending email: {} -> {}", fromEmail, request.to(), e);
            throw new MailException(MessageUtils.getMessage("mail.send.error") + e.getMessage(), e);
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
                    MessageUtils.getMessage("mail.verification.code.sent"),
                    maxAttempts,
                    expiryMinutes,
                    request.email()
            );

        } catch (Exception e) {
            logger.error("Error sending verification code: {} -> {}", fromEmail, request.email(), e);
            throw new MailException(MessageUtils.getMessage("mail.verification.code.send.error") + e.getMessage(), e);
        }
    }


    /*
    // DEPRECATED: This method is deprecated in favor of token-based password reset.
    // Use sendPasswordChangeToken() instead for secure link-based password reset.
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
    }*/

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
    public CodeSendResponse sendVerificationCodeByIdentification(CustomerIdentificationRequest request) {
        try {
            String identificationNumber = request.identificationNumber().trim();
            CustomerDto customerDto;

            if (identificationNumber.length() == 11) {
                customerDto = customerService.getCustomerByTcNumber(identificationNumber);
            } else if (identificationNumber.length() == 10) {
                customerDto = customerService.getCustomerByTaxNumber(identificationNumber);
            } else {
                throw new IllegalArgumentException(MessageUtils.getMessage("mail.identification.invalid.format"));
            }

            CodeSendRequest codeRequest = new CodeSendRequest(
                    customerDto.email(),
                    getCustomerFullNameFromDto(customerDto),
                    "Kimlik Doğrulama",
                    defaultMaxAttempts,
                    defaultExpiryMinutes,
                    "Kimlik No: " + maskIdentificationNumber(identificationNumber)
            );

            return sendVerificationCode(codeRequest);

        } catch (Exception e) {
            logger.error("Error sending verification code for identification: {}",
                    request.identificationNumber(), e);
            throw new MailException(MessageUtils.getMessage("mail.identification.send.failed") + e.getMessage(), e);
        }
    }

    private String getCustomerFullNameFromDto(CustomerDto customerDto) {
        return switch (customerDto) {
            case IndividualCustomerDto individual ->
                    individual.firstName() + " " + individual.lastName();
            case CorporateCustomerDto corporate ->
                    corporate.companyName();
        };
    }

    private String maskIdentificationNumber(String number) {
        if (number.length() >= 4) {
            return number.substring(0, 2) + "*".repeat(number.length() - 4) + number.substring(number.length() - 2);
        }
        return number;
    }

    @Override
    public boolean isEmailLimitExceeded(String email) {
        return verificationService.isEmailLimitExceeded(email);
    }

    @Override
    public PasswordChangeTokenResponse sendPasswordChangeToken(PasswordChangeTokenRequest request) {
        try {

            String token = verificationService.generateAndStorePasswordChangeToken(request.email());

            String passwordChangeUrl = "http://localhost:3000/new-password?token=" + token;
            String subject = String.format(MailConstants.Subjects.PASSWORD_RESET_FORMAT, companyName);
            String content = createPasswordChangeContent(request.email(), passwordChangeUrl);

            MimeMessage message = createMimeMessage(request.email(), subject, content, request.email());
            mailSender.send(message);

            logger.info("Password change token sent: {} -> {} (Token: {})",
                    fromEmail, request.email(), token);

            return PasswordChangeTokenResponse.success(
                    MessageUtils.getMessage("mail.password.change.token.sent")
            );

        } catch (Exception e) {
            logger.error("Error sending password change token: {} -> {}", fromEmail, request.email(), e);
            throw new MailException(MessageUtils.getMessage("mail.password.change.token.send.error") + e.getMessage(), e);
        }
    }



    private MimeMessage createMimeMessage(String to, String subject, String content, String recipientName)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, MailConstants.Config.DEFAULT_MAIL_ENCODING);

        try {
            helper.setFrom(fromEmail, fromName);
        } catch (UnsupportedEncodingException e) {
            throw new MailException(MessageUtils.getMessage("mail.send.error") + e.getMessage(), e);
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

    private String createPasswordChangeContent(String email, String passwordChangeUrl) {
        return emailTemplateService.renderPasswordChangeTemplate(email, passwordChangeUrl);
    }

}