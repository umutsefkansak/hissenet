package com.infina.hissenet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infina.hissenet.constants.MailConstants;
import com.infina.hissenet.dto.request.CodeSendRequest;
import com.infina.hissenet.dto.request.CodeVerifyRequest;
import com.infina.hissenet.dto.request.VerifyPasswordChangeTokenRequest;
import com.infina.hissenet.dto.response.CodeVerifyResponse;
import com.infina.hissenet.dto.response.VerifyPasswordChangeTokenResponse;
import com.infina.hissenet.exception.mail.MailRateLimitException;
import com.infina.hissenet.exception.mail.VerificationCodeException;
import com.infina.hissenet.entity.VerificationData;
import com.infina.hissenet.service.abstracts.IVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.infina.hissenet.constants.VerificationConstants;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationService implements IVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);
    private static final SecureRandom random = new SecureRandom();
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${mail.verification.code-length:6}")
    private int codeLength;

    @Value("${mail.verification.default-expiry-minutes:10}")
    private int defaultExpiryMinutes;

    @Value("${mail.verification.default-max-attempts:3}")
    private int defaultMaxAttempts;

    @Value("${mail.verification.ip-limit-per-hour:20}")
    private int ipLimitPerHour;

    @Value("${mail.verification.max-codes-per-day:10}")
    private int maxCodesPerDay;

    public VerificationService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAndStoreCode(CodeSendRequest request) {
        int maxAttempts = request.maxAttempts() != null ? request.maxAttempts() : defaultMaxAttempts;
        int expiryMinutes = request.expiryMinutes() != null ? request.expiryMinutes() : defaultExpiryMinutes;


        validateRateLimits(request.email());

        String code = generateSecureCode();

        VerificationData data = new VerificationData(
                request.email(),
                code,
                request.description(),
                LocalDateTime.now().plusMinutes(expiryMinutes),
                maxAttempts
        );

        storeVerificationData(request.email(), data, expiryMinutes);

        incrementEmailRateLimit(request.email());

        logger.info("Verification code generated and stored: {} (expires in {} min)",
                request.email(), expiryMinutes);

        return code;
    }

    public CodeVerifyResponse verifyCode(CodeVerifyRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);


        if (isIpRateLimited(ipAddress)) {
            logger.warn("IP rate limit exceeded: {}", ipAddress);
            return CodeVerifyResponse.failure(MailConstants.Messages.IP_LIMIT_EXCEEDED, 0);
        }


        VerificationData data = getVerificationData(request.email());
        if (data == null) {
            logger.warn("No verification code found: {}", request.email());
            return CodeVerifyResponse.failure(MailConstants.Messages.ACTIVE_CODE_NOT_FOUND, 0);
        }


        if (data.isExpired()) {
            deleteVerificationData(request.email());
            logger.warn("Verification code expired: {}", request.email());
            return CodeVerifyResponse.failure(MailConstants.Messages.ACTIVE_CODE_NOT_FOUND, 0);
        }


        if (!data.isUsable()) {
            logger.warn("Code not usable (used/blocked): {}", request.email());
            return CodeVerifyResponse.failure(MailConstants.Messages.ACTIVE_CODE_NOT_FOUND, 0);
        }


        incrementIpRateLimit(ipAddress);

        if (!data.getCode().equals(request.code())) {
            return handleIncorrectCode(data, ipAddress);
        }

        return handleCorrectCode(data, ipAddress);
    }

    private void validateRateLimits(String email) {

        String emailKey = String.format(VerificationConstants.RedisKeys.RATE_LIMIT_EMAIL_PATTERN, email);
        String countStr = redisTemplate.opsForValue().get(emailKey);
        int currentCount = countStr != null ? Integer.parseInt(countStr) : 0;

        if (currentCount >= maxCodesPerDay) {
            throw new MailRateLimitException(MailConstants.Messages.DAILY_LIMIT_EXCEEDED);
        }

        VerificationData data = getVerificationData(email);
        if (data != null && data.isBlocked() &&
                data.getBlockedAt().isAfter(LocalDateTime.now().minusHours(1))) {
            throw new VerificationCodeException(MailConstants.Messages.TOO_MANY_WRONG_ATTEMPTS);
        }
    }

    private String generateSecureCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private void storeVerificationData(String email, VerificationData data, int expiryMinutes) {
        try {
            String key = String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, email);
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, expiryMinutes, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            logger.error("Error storing verification data: {}", email, e);
            throw new VerificationCodeException("Failed to store verification code");
        }
    }

    private VerificationData getVerificationData(String email) {
        try {
            String key = String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, email);

            String json = redisTemplate.opsForValue().get(key);

            if (json == null) {
                logger.warn("No data found for key: {}", key);
                return null;
            }

            VerificationData data = objectMapper.readValue(json, VerificationData.class);
            return data;
        } catch (JsonProcessingException e) {
            logger.error("Error reading verification data: {}", email, e);
            throw new VerificationCodeException("Failed to read verification code");
        }
    }

    private void updateVerificationData(String email, VerificationData data) {
        try {
            String key = String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, email);
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttl != null && ttl > 0) {
                String json = objectMapper.writeValueAsString(data);
                redisTemplate.opsForValue().set(key, json, ttl, TimeUnit.SECONDS);
            }
        } catch (JsonProcessingException e) {
            logger.error("Error updating verification data: {}", email, e);
        }
    }

    private void deleteVerificationData(String email) {
        String key = String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, email);
        redisTemplate.delete(key);
    }

    private boolean isIpRateLimited(String ipAddress) {
        String key = String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, ipAddress);
        String countStr = redisTemplate.opsForValue().get(key);
        int currentCount = countStr != null ? Integer.parseInt(countStr) : 0;
        return currentCount >= ipLimitPerHour;
    }

    private void incrementIpRateLimit(String ipAddress) {
        String key = String.format(VerificationConstants.RedisKeys.RATE_LIMIT_IP_PATTERN, ipAddress);
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        }
    }

    private void incrementEmailRateLimit(String email) {
        String key = String.format(VerificationConstants.RedisKeys.RATE_LIMIT_EMAIL_PATTERN, email);
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }
    }

    private CodeVerifyResponse handleIncorrectCode(VerificationData data, String ipAddress) {
        data.incrementAttempt(ipAddress);
        updateVerificationData(data.getEmail(), data);

        if (data.isBlocked()) {
            logger.warn("Code blocked due to max attempts: {}", data.getEmail());

            return CodeVerifyResponse.blocked(
                    MailConstants.Messages.CODE_BLOCKED,
                    data.getBlockedAt()
            );
        }

        int remaining = data.getRemainingAttempts();

        logger.warn("Incorrect verification code: {} (remaining: {})", data.getEmail(), remaining);
        return CodeVerifyResponse.failure(
                String.format(MailConstants.Messages.WRONG_CODE_FORMAT, remaining),
                remaining
        );
    }

    private CodeVerifyResponse handleCorrectCode(VerificationData data, String ipAddress) {
        data.markAsUsed(ipAddress);
        updateVerificationData(data.getEmail(), data);

        logger.info("Verification code successfully validated: {}", data.getEmail());
        return CodeVerifyResponse.success(MailConstants.Messages.CODE_VERIFIED_SUCCESS);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    public boolean isEmailLimitExceeded(String email) {
        String key = String.format(VerificationConstants.RedisKeys.RATE_LIMIT_EMAIL_PATTERN, email);
        String countStr = redisTemplate.opsForValue().get(key);
        int currentCount = countStr != null ? Integer.parseInt(countStr) : 0;
        return currentCount >= maxCodesPerDay;
    }
    public void clearVerificationCode(String email) {
        String key = String.format(VerificationConstants.RedisKeys.VERIFICATION_CODE_PATTERN, email);
        redisTemplate.delete(key);
        logger.info("Cleared verification code for: {}", email);
    }

    @Override
    public String generateAndStorePasswordChangeToken(String email) {
        try {
            String token = UUID.randomUUID().toString();
            String redisKey = String.format(VerificationConstants.RedisKeys.PASSWORD_CHANGE_TOKEN_PATTERN, token);            redisTemplate.opsForValue().set(redisKey, email, 10, TimeUnit.MINUTES);

            logger.info("Password change token generated and stored for: {}", email);
            return token;

        } catch (Exception e) {
            logger.error("Error generating password change token for: {}", email, e);
            throw new VerificationCodeException("Failed to generate password change token");
        }
    }

    @Override
    public VerifyPasswordChangeTokenResponse verifyPasswordChangeToken(VerifyPasswordChangeTokenRequest request) {
        try {
            String redisKey = String.format(VerificationConstants.RedisKeys.PASSWORD_CHANGE_TOKEN_PATTERN, request.token());            String email = redisTemplate.opsForValue().get(redisKey);

            if (email == null) {
                logger.warn("Invalid or expired password change token: {}", request.token());
                return VerifyPasswordChangeTokenResponse.failure(MailConstants.Messages.INVALID_PASSWORD_CHANGE_TOKEN);
            }

            logger.info("Password change token verified successfully: {}", request.token());
            return VerifyPasswordChangeTokenResponse.success(
                    MailConstants.Messages.PASSWORD_CHANGE_TOKEN_VALID,
                    email
            );

        } catch (Exception e) {
            logger.error("Error verifying password change token: {}", request.token(), e);
            throw new VerificationCodeException("Failed to verify password change token");
        }
    }

    @Override
    public void clearPasswordChangeToken(String token) {
        String key = String.format(VerificationConstants.RedisKeys.PASSWORD_CHANGE_TOKEN_PATTERN, token);
        redisTemplate.delete(key);
        logger.info("Cleared password change token: {}", token);
    }
}