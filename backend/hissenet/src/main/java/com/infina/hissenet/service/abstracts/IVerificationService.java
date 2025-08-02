package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.CodeSendRequest;
import com.infina.hissenet.dto.request.CodeVerifyRequest;
import com.infina.hissenet.dto.response.CodeVerifyResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service interface for handling email verification codes.
 * Provides functionality for generating, storing, and verifying verification codes
 * with rate limiting and security features.
 * Uses Redis for storing verification codes and managing rate limits.
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 */
public interface IVerificationService {

    /**
     * Generates a secure verification code and stores it with the given parameters.
     * Validates rate limits before generating the code.
     *
     * @param request the code generation request containing email, description, and configuration
     * @return the generated verification code
     * @throws com.infina.hissenet.exception.mail.MailRateLimitException if rate limits are exceeded
     * @throws com.infina.hissenet.exception.mail.VerificationCodeException if verification is blocked
     */
    String generateAndStoreCode(CodeSendRequest request);

    /**
     * Verifies the provided verification code against stored data.
     * Handles rate limiting, expiration checks, and attempt tracking.
     *
     * @param request the verification request containing email and code
     * @param httpRequest the HTTP request for IP address extraction
     * @return verification response with success/failure status and remaining attempts
     */
    CodeVerifyResponse verifyCode(CodeVerifyRequest request, HttpServletRequest httpRequest);

    /**
     * Checks if the email has exceeded the daily code generation limit.
     *
     * @param email the email address to check
     * @return true if the daily limit is exceeded, false otherwise
     */
    boolean isEmailLimitExceeded(String email);

    /**
     * Clears the verification code for the specified email address.
     * Removes the code from storage immediately.
     *
     * @param email the email address whose verification code should be cleared
     */
    void clearVerificationCode(String email);
}