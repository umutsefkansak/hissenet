package com.infina.hissenet.service;

import com.infina.hissenet.dto.common.CorporateCustomerDto;
import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.dto.common.IndividualCustomerDto;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.exception.mail.MailException;
import com.infina.hissenet.service.abstracts.ICustomerService;
import com.infina.hissenet.service.abstracts.IEmailTemplateService;
import com.infina.hissenet.service.abstracts.IVerificationService;
import com.infina.hissenet.utils.MessageUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private IEmailTemplateService emailTemplateService;

    @Mock
    private IVerificationService verificationService;

    @Mock
    private ICustomerService customerService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private MailService mailService;

    private Session mailSession;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mailService, "fromEmail", "test@hissenet.com");
        ReflectionTestUtils.setField(mailService, "fromName", "HisseNet Test");
        ReflectionTestUtils.setField(mailService, "defaultExpiryMinutes", 10);
        ReflectionTestUtils.setField(mailService, "defaultMaxAttempts", 3);
        ReflectionTestUtils.setField(mailService, "companyName", "HisseNet");

        mailSession = Session.getDefaultInstance(new Properties());
        mimeMessage = new MimeMessage(mailSession);
    }

    @Test
    void sendMail_WhenValidRequest_ShouldSendSuccessfully() {
        // Given
        MailSendRequest request = new MailSendRequest(
                "test@example.com",
                "Test Subject",
                "Test Content",
                "Test User"
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.send.success"))
                    .thenReturn("Mail sent successfully");

            // When
            MailSendResponse response = mailService.sendMail(request);

            // Then
            assertTrue(response.success());
            assertEquals("Mail sent successfully", response.message());
            verify(mailSender).send(mimeMessage);
        }
    }

    @Test
    void sendMail_WhenMessagingException_ShouldThrowMailException() {
        // Given
        MailSendRequest request = new MailSendRequest(
                "invalid-email",
                "Test Subject",
                "Test Content",
                "Test User"
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(MimeMessage.class));

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.send.error"))
                    .thenReturn("Mail sending failed: ");

            // When & Then
            MailException exception = assertThrows(MailException.class,
                    () -> mailService.sendMail(request));
            assertTrue(exception.getMessage().contains("Mail sending failed"));
        }
    }

    @Test
    void sendVerificationCode_WhenValidRequest_ShouldSendSuccessfully() {
        // Given
        CodeSendRequest request = new CodeSendRequest(
                "test@example.com",
                "Test User",
                "Test Description",
                3,
                10,
                "Additional Info"
        );

        String generatedCode = "123456";
        String emailTemplate = "<html>Verification code: " + generatedCode + "</html>";

        when(verificationService.generateAndStoreCode(request)).thenReturn(generatedCode);
        when(emailTemplateService.renderVerificationTemplate(
                anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(emailTemplate);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.verification.code.sent"))
                    .thenReturn("Verification code sent");

            // When
            CodeSendResponse response = mailService.sendVerificationCode(request);

            // Then
            assertTrue(response.success());
            assertEquals("Verification code sent", response.message());
            assertEquals(3, response.maxAttempts());
            assertEquals(10, response.expiryMinutes());
            assertEquals("test@example.com", response.email());

            verify(verificationService).generateAndStoreCode(request);
            verify(emailTemplateService).renderVerificationTemplate(
                    "Test User", generatedCode, "Test Description",
                    "Additional Info", 3, 10);
            verify(mailSender).send(mimeMessage);
        }
    }

    @Test
    void sendVerificationCode_WhenNullValuesProvided_ShouldUseDefaultValues() {
        // Given
        CodeSendRequest request = new CodeSendRequest(
                "test@example.com",
                "Test User",
                "Test Description",
                null,
                null,
                "Additional Info"
        );

        String generatedCode = "123456";
        when(verificationService.generateAndStoreCode(request)).thenReturn(generatedCode);
        when(emailTemplateService.renderVerificationTemplate(
                anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn("template");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.verification.code.sent"))
                    .thenReturn("Verification code sent");

            // When
            CodeSendResponse response = mailService.sendVerificationCode(request);

            // Then
            assertEquals(3, response.maxAttempts());
            assertEquals(10, response.expiryMinutes());
            verify(emailTemplateService).renderVerificationTemplate(
                    "Test User", generatedCode, "Test Description",
                    "Additional Info", 3, 10);
        }
    }

    @Test
    void sendVerificationCode_WhenServiceError_ShouldThrowMailException() {
        // Given
        CodeSendRequest request = new CodeSendRequest(
                "test@example.com",
                "Test User",
                "Test Description",
                3, 10, "Additional Info"
        );

        when(verificationService.generateAndStoreCode(request))
                .thenThrow(new RuntimeException("Redis error"));

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.verification.code.send.error"))
                    .thenReturn("Verification code send failed: ");

            // When & Then
            MailException exception = assertThrows(MailException.class,
                    () -> mailService.sendVerificationCode(request));
            assertTrue(exception.getMessage().contains("Verification code send failed"));
        }
    }

    @Test
    void verifyCode_WhenValidRequest_ShouldCallVerificationService() {
        // Given
        CodeVerifyRequest request = new CodeVerifyRequest("test@example.com", "123456");
        CodeVerifyResponse expectedResponse = CodeVerifyResponse.success("Code verified");

        when(verificationService.verifyCode(request, httpServletRequest))
                .thenReturn(expectedResponse);

        // When
        CodeVerifyResponse response = mailService.verifyCode(request, httpServletRequest);

        // Then
        assertEquals(expectedResponse, response);
        verify(verificationService).verifyCode(request, httpServletRequest);
    }

    @Test
    void sendNotification_WhenValidRequest_ShouldSendSuccessfully() {
        // Given
        NotificationSendRequest request = new NotificationSendRequest(
                "test@example.com",
                "Test User",
                "Test notification message",
                "Custom Title"
        );

        String notificationTemplate = "<html>Notification content</html>";
        when(emailTemplateService.renderNotificationTemplate("Test User", "Test notification message"))
                .thenReturn(notificationTemplate);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        mailService.sendNotification(request);

        // Then
        verify(emailTemplateService).renderNotificationTemplate("Test User", "Test notification message");
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendNotification_WhenTitleIsNull_ShouldUseDefaultTitle() {
        // Given
        NotificationSendRequest request = new NotificationSendRequest(
                "test@example.com",
                "Test User",
                "Test notification message",
                null
        );

        when(emailTemplateService.renderNotificationTemplate(anyString(), anyString()))
                .thenReturn("template");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        mailService.sendNotification(request);

        // Then
        verify(emailTemplateService).renderNotificationTemplate("Test User", "Test notification message");
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendVerificationCodeByIdentification_WhenIndividualCustomer_ShouldSendSuccessfully() {
        // Given
        CustomerIdentificationRequest request = new CustomerIdentificationRequest("12345678901");
        IndividualCustomerDto customerDto = new IndividualCustomerDto(
                1L, "CUST001", "test@example.com", "5551234567", "TR", true, "INDIVIDUAL",
                "John", "Middle", "Doe", "12345678901", LocalDate.of(1990, 1, 1),
                "Istanbul", "M", "Jane", "Jack", "Engineer", "University", null, null, null
        );

        when(customerService.getCustomerByTcNumber("12345678901")).thenReturn(customerDto);
        when(verificationService.generateAndStoreCode(any(CodeSendRequest.class))).thenReturn("123456");
        when(emailTemplateService.renderVerificationTemplate(
                anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn("template");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.verification.code.sent"))
                    .thenReturn("Verification code sent");

            // When
            CodeSendResponse response = mailService.sendVerificationCodeByIdentification(request);

            // Then
            assertTrue(response.success());
            verify(customerService).getCustomerByTcNumber("12345678901");

            ArgumentCaptor<CodeSendRequest> codeRequestCaptor = ArgumentCaptor.forClass(CodeSendRequest.class);
            verify(verificationService).generateAndStoreCode(codeRequestCaptor.capture());

            CodeSendRequest capturedRequest = codeRequestCaptor.getValue();
            assertEquals("test@example.com", capturedRequest.email());
            assertEquals("John Doe", capturedRequest.recipientName());
            assertTrue(capturedRequest.additionalInfo().contains("12***"));
        }
    }

    @Test
    void sendVerificationCodeByIdentification_WhenCorporateCustomer_ShouldSendSuccessfully() {
        // Given
        CustomerIdentificationRequest request = new CustomerIdentificationRequest("1234567890");
        CorporateCustomerDto customerDto = new CorporateCustomerDto(
                1L, "CORP001", "corp@example.com", "5551234567", "TR", true, "CORPORATE",
                "Test Company", "1234567890", "TR123", LocalDate.of(2000, 1, 1),
                "Technology", "John Doe", "CEO", "www.test.com", null,
                "5551111111", "12345678901", "john@test.com", "Test Tax Office"
        );

        when(customerService.getCustomerByTaxNumber("1234567890")).thenReturn(customerDto);
        when(verificationService.generateAndStoreCode(any(CodeSendRequest.class))).thenReturn("123456");
        when(emailTemplateService.renderVerificationTemplate(
                anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn("template");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.verification.code.sent"))
                    .thenReturn("Verification code sent");

            // When
            CodeSendResponse response = mailService.sendVerificationCodeByIdentification(request);

            // Then
            assertTrue(response.success());
            verify(customerService).getCustomerByTaxNumber("1234567890");

            ArgumentCaptor<CodeSendRequest> codeRequestCaptor = ArgumentCaptor.forClass(CodeSendRequest.class);
            verify(verificationService).generateAndStoreCode(codeRequestCaptor.capture());

            CodeSendRequest capturedRequest = codeRequestCaptor.getValue();
            assertEquals("corp@example.com", capturedRequest.email());
            assertEquals("Test Company", capturedRequest.recipientName());
            assertTrue(capturedRequest.additionalInfo().contains("12****"));
        }
    }

    @Test
    void sendVerificationCodeByIdentification_WhenInvalidFormat_ShouldThrowException() {
        // Given
        CustomerIdentificationRequest request = new CustomerIdentificationRequest("123");

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.identification.invalid.format"))
                    .thenReturn("Invalid identification format");
            messageUtils.when(() -> MessageUtils.getMessage("mail.identification.send.failed"))
                    .thenReturn("Identification send failed: ");

            // When & Then
            MailException exception = assertThrows(MailException.class,
                    () -> mailService.sendVerificationCodeByIdentification(request));
            assertTrue(exception.getMessage().contains("Identification send failed"));
        }
    }

    @Test
    void isEmailLimitExceeded_WhenCalled_ShouldCallVerificationService() {
        // Given
        String email = "test@example.com";
        when(verificationService.isEmailLimitExceeded(email)).thenReturn(true);

        // When
        boolean result = mailService.isEmailLimitExceeded(email);

        // Then
        assertTrue(result);
        verify(verificationService).isEmailLimitExceeded(email);
    }

    @Test
    void sendPasswordChangeToken_WhenValidRequest_ShouldSendSuccessfully() {
        // Given
        PasswordChangeTokenRequest request = new PasswordChangeTokenRequest("test@example.com");
        String token = "test-token-123";

        when(verificationService.generateAndStorePasswordChangeToken("test@example.com"))
                .thenReturn(token);
        when(emailTemplateService.renderPasswordChangeTemplate(
                eq("test@example.com"), anyString()))
                .thenReturn("password change template");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.password.change.token.sent"))
                    .thenReturn("Password change token sent");

            // When
            PasswordChangeTokenResponse response = mailService.sendPasswordChangeToken(request);

            // Then
            assertEquals("Password change token sent", response.message());
            verify(verificationService).generateAndStorePasswordChangeToken("test@example.com");
            verify(emailTemplateService).renderPasswordChangeTemplate(
                    eq("test@example.com"), contains("localhost:3000/new-password?token=" + token));
            verify(mailSender).send(mimeMessage);
        }
    }

    @Test
    void sendPasswordChangeToken_WhenServiceError_ShouldThrowMailException() {
        // Given
        PasswordChangeTokenRequest request = new PasswordChangeTokenRequest("test@example.com");

        when(verificationService.generateAndStorePasswordChangeToken("test@example.com"))
                .thenThrow(new RuntimeException("Token generation failed"));

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.password.change.token.send.error"))
                    .thenReturn("Password change token send failed: ");

            // When & Then
            MailException exception = assertThrows(MailException.class,
                    () -> mailService.sendPasswordChangeToken(request));
            assertTrue(exception.getMessage().contains("Password change token send failed"));
        }
    }

    @Test
    void createVerificationSubject_WhenDescriptionProvided_ShouldUseDescription() {
        // Given
        String description = "Login Verification";

        // When
        CodeSendRequest request = new CodeSendRequest(
                "test@example.com", "Test User", description, 3, 10, "Additional Info"
        );

        when(verificationService.generateAndStoreCode(request)).thenReturn("123456");
        when(emailTemplateService.renderVerificationTemplate(
                anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn("template");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.verification.code.sent"))
                    .thenReturn("Verification code sent");

            // When
            mailService.sendVerificationCode(request);

            // Then
            verify(mailSender).send(any(MimeMessage.class));
        }
    }

    @Test
    void maskIdentificationNumber_WhenCalled_ShouldMaskCorrectly() {
        // Given
        CustomerIdentificationRequest request = new CustomerIdentificationRequest("12345678901");
        IndividualCustomerDto customerDto = new IndividualCustomerDto(
                1L, "CUST001", "test@example.com", "5551234567", "TR", true, "INDIVIDUAL",
                "John", null, "Doe", "12345678901", LocalDate.of(1990, 1, 1),
                "Istanbul", "M", "Jane", "Jack", "Engineer", "University", null, null, null
        );

        when(customerService.getCustomerByTcNumber("12345678901")).thenReturn(customerDto);
        when(verificationService.generateAndStoreCode(any(CodeSendRequest.class))).thenReturn("123456");
        when(emailTemplateService.renderVerificationTemplate(
                anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn("template");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.verification.code.sent"))
                    .thenReturn("Verification code sent");

            // When
            mailService.sendVerificationCodeByIdentification(request);

            // Then
            ArgumentCaptor<CodeSendRequest> codeRequestCaptor = ArgumentCaptor.forClass(CodeSendRequest.class);
            verify(verificationService).generateAndStoreCode(codeRequestCaptor.capture());

            CodeSendRequest capturedRequest = codeRequestCaptor.getValue();
            assertTrue(capturedRequest.additionalInfo().contains("12*******01"));
        }
    }

}