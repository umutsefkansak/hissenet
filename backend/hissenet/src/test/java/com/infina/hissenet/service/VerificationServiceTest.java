package com.infina.hissenet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infina.hissenet.constants.MailConstants;
import com.infina.hissenet.constants.VerificationConstants;
import com.infina.hissenet.dto.request.CodeSendRequest;
import com.infina.hissenet.dto.request.CodeVerifyRequest;
import com.infina.hissenet.dto.request.VerifyPasswordChangeTokenRequest;
import com.infina.hissenet.dto.response.CodeVerifyResponse;
import com.infina.hissenet.dto.response.VerifyPasswordChangeTokenResponse;
import com.infina.hissenet.entity.VerificationData;
import com.infina.hissenet.exception.mail.MailRateLimitException;
import com.infina.hissenet.exception.mail.VerificationCodeException;
import com.infina.hissenet.utils.MessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private VerificationService verificationService;

    private ObjectMapper objectMapper;
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_IP = "192.168.1.1";
    private final String TEST_CODE = "123456";
    private final String TEST_TOKEN = "test-token-123";

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(verificationService, "codeLength", 6);
        ReflectionTestUtils.setField(verificationService, "defaultExpiryMinutes", 10);
        ReflectionTestUtils.setField(verificationService, "defaultMaxAttempts", 3);
        ReflectionTestUtils.setField(verificationService, "ipLimitPerHour", 20);
        ReflectionTestUtils.setField(verificationService, "maxCodesPerDay", 10);

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void generateAndStoreCode_WhenValidRequest_ShouldGenerateAndStoreCode() throws JsonProcessingException {
        // Given
        CodeSendRequest request = new CodeSendRequest(
                TEST_EMAIL, "Test User", "Test Description", 3, 10, "Additional Info"
        );

        when(valueOperations.get(anyString())).thenReturn(null);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        // When
        String generatedCode = verificationService.generateAndStoreCode(request);

        // Then
        assertNotNull(generatedCode);
        assertEquals(6, generatedCode.length());
        assertTrue(generatedCode.matches("\\d{6}"));

        verify(valueOperations).set(
                eq(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL)),
                anyString(),
                eq(10L),
                eq(TimeUnit.MINUTES)
        );
        verify(valueOperations).increment(
                eq(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_EMAIL_PATTERN, TEST_EMAIL))
        );
        verify(redisTemplate).expire(
                eq(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_EMAIL_PATTERN, TEST_EMAIL)),
                eq(1L),
                eq(TimeUnit.DAYS)
        );
    }

    @Test
    void generateAndStoreCode_WhenNullValues_ShouldUseDefaults() {
        // Given
        CodeSendRequest request = new CodeSendRequest(
                TEST_EMAIL, "Test User", "Test Description", null, null, "Additional Info"
        );

        when(valueOperations.get(anyString())).thenReturn(null);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        // When
        String generatedCode = verificationService.generateAndStoreCode(request);

        // Then
        assertNotNull(generatedCode);
        verify(valueOperations).set(
                anyString(),
                anyString(),
                eq(10L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    void generateAndStoreCode_WhenDailyLimitExceeded_ShouldThrowRateLimitException() {
        // Given
        CodeSendRequest request = new CodeSendRequest(
                TEST_EMAIL, "Test User", "Test Description", 3, 10, "Additional Info"
        );

        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_EMAIL_PATTERN, TEST_EMAIL)))
                .thenReturn("10");

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.daily.limit.exceeded"))
                    .thenReturn("Daily limit exceeded");

            // When & Then
            assertThrows(MailRateLimitException.class,
                    () -> verificationService.generateAndStoreCode(request));
        }
    }

    @Test
    void generateAndStoreCode_WhenBlockedWithinHour_ShouldThrowVerificationCodeException() throws JsonProcessingException {
        // Given
        CodeSendRequest request = new CodeSendRequest(
                TEST_EMAIL, "Test User", "Test Description", 3, 10, "Additional Info"
        );

        VerificationData blockedData = new VerificationData(
                TEST_EMAIL, "123456", "Test", LocalDateTime.now().plusMinutes(10), 3
        );
        blockedData.setBlocked(true);
        blockedData.setBlockedAt(LocalDateTime.now().minusMinutes(30)); // Blocked 30 minutes ago

        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_EMAIL_PATTERN, TEST_EMAIL)))
                .thenReturn("5"); // Under daily limit
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL)))
                .thenReturn(objectMapper.writeValueAsString(blockedData));

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("mail.too.many.wrong.attempts"))
                    .thenReturn("Too many wrong attempts");

            // When & Then
            assertThrows(VerificationCodeException.class,
                    () -> verificationService.generateAndStoreCode(request));
        }
    }

    @Test
    void verifyCode_WhenValidCode_ShouldReturnSuccess() throws JsonProcessingException {
        // Given
        CodeVerifyRequest request = new CodeVerifyRequest(TEST_EMAIL, TEST_CODE);

        VerificationData validData = new VerificationData(
                TEST_EMAIL, TEST_CODE, "Test", LocalDateTime.now().plusMinutes(5), 3
        );

        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpServletRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(httpServletRequest.getRemoteAddr()).thenReturn(TEST_IP);

        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, TEST_IP)))
                .thenReturn("5"); // Under IP limit
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL)))
                .thenReturn(objectMapper.writeValueAsString(validData));
        when(valueOperations.increment(anyString())).thenReturn(6L);
        when(redisTemplate.getExpire(anyString(), eq(TimeUnit.SECONDS))).thenReturn(300L);

        // When
        CodeVerifyResponse response = verificationService.verifyCode(request, httpServletRequest);

        // Then
        assertTrue(response.success());
        assertEquals(MailConstants.Messages.CODE_VERIFIED_SUCCESS, response.message());

        verify(valueOperations).increment(
                eq(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, TEST_IP))
        );
    }

    @Test
    void verifyCode_WhenIpRateLimited_ShouldReturnFailure() {
        // Given
        CodeVerifyRequest request = new CodeVerifyRequest(TEST_EMAIL, TEST_CODE);

        when(httpServletRequest.getRemoteAddr()).thenReturn(TEST_IP);
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, TEST_IP)))
                .thenReturn("20"); // At IP limit

        // When
        CodeVerifyResponse response = verificationService.verifyCode(request, httpServletRequest);

        // Then
        assertFalse(response.success());
        assertEquals(MailConstants.Messages.IP_LIMIT_EXCEEDED, response.message());
        assertEquals(0, response.remainingAttempts());
    }

    @Test
    void verifyCode_WhenNoCodeFound_ShouldReturnFailure() {
        // Given
        CodeVerifyRequest request = new CodeVerifyRequest(TEST_EMAIL, TEST_CODE);

        when(httpServletRequest.getRemoteAddr()).thenReturn(TEST_IP);
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, TEST_IP)))
                .thenReturn("5");
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL)))
                .thenReturn(null);

        // When
        CodeVerifyResponse response = verificationService.verifyCode(request, httpServletRequest);

        // Then
        assertFalse(response.success());
        assertEquals(MailConstants.Messages.ACTIVE_CODE_NOT_FOUND, response.message());
    }

    @Test
    void verifyCode_WhenCodeExpired_ShouldReturnFailureAndDeleteCode() throws JsonProcessingException {
        // Given
        CodeVerifyRequest request = new CodeVerifyRequest(TEST_EMAIL, TEST_CODE);

        VerificationData expiredData = new VerificationData(
                TEST_EMAIL, TEST_CODE, "Test", LocalDateTime.now().minusMinutes(5), 3 // Expired 5 minutes ago
        );

        when(httpServletRequest.getRemoteAddr()).thenReturn(TEST_IP);
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, TEST_IP)))
                .thenReturn("5");
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL)))
                .thenReturn(objectMapper.writeValueAsString(expiredData));

        // When
        CodeVerifyResponse response = verificationService.verifyCode(request, httpServletRequest);

        // Then
        assertFalse(response.success());
        assertEquals(MailConstants.Messages.ACTIVE_CODE_NOT_FOUND, response.message());

        verify(redisTemplate).delete(
                eq(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL))
        );
    }

    @Test
    void verifyCode_WhenIncorrectCode_ShouldReturnFailureAndIncrementAttempts() throws JsonProcessingException {
        // Given
        CodeVerifyRequest request = new CodeVerifyRequest(TEST_EMAIL, "wrong-code");

        VerificationData validData = new VerificationData(
                TEST_EMAIL, TEST_CODE, "Test", LocalDateTime.now().plusMinutes(5), 3
        );

        when(httpServletRequest.getRemoteAddr()).thenReturn(TEST_IP);
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, TEST_IP)))
                .thenReturn("5");
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL)))
                .thenReturn(objectMapper.writeValueAsString(validData));
        when(valueOperations.increment(anyString())).thenReturn(6L);
        when(redisTemplate.getExpire(anyString(), eq(TimeUnit.SECONDS))).thenReturn(300L);

        // When
        CodeVerifyResponse response = verificationService.verifyCode(request, httpServletRequest);

        // Then
        assertFalse(response.success());
        assertTrue(response.message().contains("2"));
        assertEquals(2, response.remainingAttempts());
    }

    @Test
    void verifyCode_WhenMaxAttemptsReached_ShouldReturnBlockedResponse() throws JsonProcessingException {
        // Given
        CodeVerifyRequest request = new CodeVerifyRequest(TEST_EMAIL, "wrong-code");

        VerificationData dataWithTwoAttempts = new VerificationData(
                TEST_EMAIL, TEST_CODE, "Test", LocalDateTime.now().plusMinutes(5), 3
        );
        dataWithTwoAttempts.setAttemptCount(2);

        when(httpServletRequest.getRemoteAddr()).thenReturn(TEST_IP);
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, TEST_IP)))
                .thenReturn("5");
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL)))
                .thenReturn(objectMapper.writeValueAsString(dataWithTwoAttempts));
        when(valueOperations.increment(anyString())).thenReturn(6L);
        when(redisTemplate.getExpire(anyString(), eq(TimeUnit.SECONDS))).thenReturn(300L);

        // When
        CodeVerifyResponse response = verificationService.verifyCode(request, httpServletRequest);

        // Then
        assertFalse(response.success());
        assertTrue(response.blocked());
        assertEquals(MailConstants.Messages.CODE_BLOCKED, response.message());
        assertNotNull(response.blockedUntil());
    }

    @Test
    void verifyCode_WhenCodeAlreadyUsed_ShouldReturnFailure() throws JsonProcessingException {
        // Given
        CodeVerifyRequest request = new CodeVerifyRequest(TEST_EMAIL, TEST_CODE);

        VerificationData usedData = new VerificationData(
                TEST_EMAIL, TEST_CODE, "Test", LocalDateTime.now().plusMinutes(5), 3
        );
        usedData.setUsed(true);

        when(httpServletRequest.getRemoteAddr()).thenReturn(TEST_IP);
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, TEST_IP)))
                .thenReturn("5");
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL)))
                .thenReturn(objectMapper.writeValueAsString(usedData));

        // When
        CodeVerifyResponse response = verificationService.verifyCode(request, httpServletRequest);

        // Then
        assertFalse(response.success());
        assertEquals(MailConstants.Messages.ACTIVE_CODE_NOT_FOUND, response.message());
    }

    @Test
    void verifyCode_WhenXForwardedForHeaderExists_ShouldUseCorrectIp() throws JsonProcessingException {
        // Given
        CodeVerifyRequest request = new CodeVerifyRequest(TEST_EMAIL, TEST_CODE);
        String forwardedIp = "10.0.0.1";

        VerificationData validData = new VerificationData(
                TEST_EMAIL, TEST_CODE, "Test", LocalDateTime.now().plusMinutes(5), 3
        );

        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(forwardedIp + ", 192.168.1.1");
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, forwardedIp)))
                .thenReturn("5");
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL)))
                .thenReturn(objectMapper.writeValueAsString(validData));
        when(valueOperations.increment(anyString())).thenReturn(6L);
        when(redisTemplate.getExpire(anyString(), eq(TimeUnit.SECONDS))).thenReturn(300L);

        // When
        CodeVerifyResponse response = verificationService.verifyCode(request, httpServletRequest);

        // Then
        assertTrue(response.success());
        verify(valueOperations).increment(
                eq(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, forwardedIp))
        );
    }

    @Test
    void isEmailLimitExceeded_WhenUnderLimit_ShouldReturnFalse() {
        // Given
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_EMAIL_PATTERN, TEST_EMAIL)))
                .thenReturn("5");

        // When
        boolean result = verificationService.isEmailLimitExceeded(TEST_EMAIL);

        // Then
        assertFalse(result);
    }

    @Test
    void isEmailLimitExceeded_WhenAtLimit_ShouldReturnTrue() {
        // Given
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_EMAIL_PATTERN, TEST_EMAIL)))
                .thenReturn("10");

        // When
        boolean result = verificationService.isEmailLimitExceeded(TEST_EMAIL);

        // Then
        assertTrue(result);
    }

    @Test
    void isEmailLimitExceeded_WhenNoData_ShouldReturnFalse() {
        // Given
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_EMAIL_PATTERN, TEST_EMAIL)))
                .thenReturn(null);

        // When
        boolean result = verificationService.isEmailLimitExceeded(TEST_EMAIL);

        // Then
        assertFalse(result);
    }

    @Test
    void clearVerificationCode_WhenCalled_ShouldDeleteFromRedis() {
        // When
        verificationService.clearVerificationCode(TEST_EMAIL);

        // Then
        verify(redisTemplate).delete(
                eq(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL))
        );
    }

    @Test
    void generateAndStorePasswordChangeToken_WhenCalled_ShouldGenerateAndStoreToken() {
        // Given
        doNothing().when(valueOperations).set(anyString(), eq(TEST_EMAIL), eq(10L), eq(TimeUnit.MINUTES));

        // When
        String token = verificationService.generateAndStorePasswordChangeToken(TEST_EMAIL);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        verify(valueOperations).set(
                eq(String.format(VerificationConstants.RedisKeys.PASSWORD_CHANGE_TOKEN_PATTERN, token)),
                eq(TEST_EMAIL),
                eq(10L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    void generateAndStorePasswordChangeToken_WhenRedisError_ShouldThrowException() {
        // Given
        doThrow(new RuntimeException("Redis error"))
                .when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("verification.password.token.generate.failed"))
                    .thenReturn("Token generation failed");

            // When & Then
            assertThrows(VerificationCodeException.class,
                    () -> verificationService.generateAndStorePasswordChangeToken(TEST_EMAIL));
        }
    }

    @Test
    void verifyPasswordChangeToken_WhenValidToken_ShouldReturnSuccess() {
        // Given
        VerifyPasswordChangeTokenRequest request = new VerifyPasswordChangeTokenRequest(TEST_TOKEN);

        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.PASSWORD_CHANGE_TOKEN_PATTERN, TEST_TOKEN)))
                .thenReturn(TEST_EMAIL);

        // When
        VerifyPasswordChangeTokenResponse response = verificationService.verifyPasswordChangeToken(request);

        // Then
        assertTrue(response.valid());
        assertEquals(MailConstants.Messages.PASSWORD_CHANGE_TOKEN_VALID, response.message());
        assertEquals(TEST_EMAIL, response.email());
    }

    @Test
    void verifyPasswordChangeToken_WhenInvalidToken_ShouldReturnFailure() {
        // Given
        VerifyPasswordChangeTokenRequest request = new VerifyPasswordChangeTokenRequest(TEST_TOKEN);

        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.PASSWORD_CHANGE_TOKEN_PATTERN, TEST_TOKEN)))
                .thenReturn(null);

        // When
        VerifyPasswordChangeTokenResponse response = verificationService.verifyPasswordChangeToken(request);

        // Then
        assertFalse(response.valid());
        assertEquals(MailConstants.Messages.INVALID_PASSWORD_CHANGE_TOKEN, response.message());
        assertNull(response.email());
    }

    @Test
    void verifyPasswordChangeToken_WhenRedisError_ShouldThrowException() {
        // Given
        VerifyPasswordChangeTokenRequest request = new VerifyPasswordChangeTokenRequest(TEST_TOKEN);

        when(valueOperations.get(anyString())).thenThrow(new RuntimeException("Redis error"));

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("verification.password.token.generate.failed"))
                    .thenReturn("Token verification failed");

            // When & Then
            assertThrows(VerificationCodeException.class,
                    () -> verificationService.verifyPasswordChangeToken(request));
        }
    }

    @Test
    void clearPasswordChangeToken_WhenCalled_ShouldDeleteFromRedis() {
        // When
        verificationService.clearPasswordChangeToken(TEST_TOKEN);

        // Then
        verify(redisTemplate).delete(
                eq(String.format(VerificationConstants.RedisKeys.PASSWORD_CHANGE_TOKEN_PATTERN, TEST_TOKEN))
        );
    }

    @Test
    void verifyCode_WhenJsonProcessingError_ShouldThrowVerificationCodeException() {
        // Given
        CodeVerifyRequest request = new CodeVerifyRequest(TEST_EMAIL, TEST_CODE);

        when(httpServletRequest.getRemoteAddr()).thenReturn(TEST_IP);
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, TEST_IP)))
                .thenReturn("5");
        when(valueOperations.get(String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, TEST_EMAIL)))
                .thenReturn("invalid-json");

        try (MockedStatic<MessageUtils> messageUtils = mockStatic(MessageUtils.class)) {
            messageUtils.when(() -> MessageUtils.getMessage("verification.code.read.failed"))
                    .thenReturn("Failed to read verification code");

            // When & Then
            assertThrows(VerificationCodeException.class,
                    () -> verificationService.verifyCode(request, httpServletRequest));
        }
    }

    @Test
    void generateAndStoreCode_WhenRedisConnectionFails_ShouldThrowRuntimeException() {
        // Given
        CodeSendRequest request = new CodeSendRequest(
                TEST_EMAIL, "Test User", "Test Description", 3, 10, "Additional Info"
        );

        when(valueOperations.get(anyString())).thenReturn(null); // No rate limits
        lenient().when(valueOperations.increment(anyString())).thenReturn(1L);


        doThrow(new RuntimeException("Redis connection failed"))
                .when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> verificationService.generateAndStoreCode(request));

        assertEquals("Redis connection failed", exception.getMessage());
    }
}