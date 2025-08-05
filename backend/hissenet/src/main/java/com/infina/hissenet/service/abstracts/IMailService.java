package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service interface for email operations including verification codes and notifications.
 * Handles email sending, verification code generation/validation, and mail management.
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 */
public interface IMailService {

    /**
     * Sends a basic email with specified content.
     *
     * @param request the mail send request containing recipient, subject and content
     * @return response indicating success or failure of mail sending
     */
    MailSendResponse sendMail(MailSendRequest request);

    /**
     * Generates and sends a verification code to the specified email address.
     * Includes rate limiting and validation rules.
     *
     * @param request the verification code send request with email and options
     * @return response with sending status and attempt/expiry details
     */
    CodeSendResponse sendVerificationCode(CodeSendRequest request);

    /**
     * Verifies a submitted code against the active verification code for an email.
     * Tracks attempts and applies blocking rules for security.
     *
     * @param request the verification request containing email and code
     * @param httpRequest the HTTP request for IP tracking
     * @return response indicating verification success, failure, or blocking status
     */
    CodeVerifyResponse verifyCode(CodeVerifyRequest request, HttpServletRequest httpRequest);

    /**
     * Sends a notification email with custom message content.
     *
     * @param request the notification request with recipient and message details
     */
    void sendNotification(NotificationSendRequest request);

    /**
     * Checks if an email address has exceeded the daily verification code limit.
     *
     * @param email the email address to check
     * @return true if limit is exceeded, false otherwise
     */
    boolean isEmailLimitExceeded(String email);

    /**
     * Sends a password reset verification code with security protections.
     * Uses asynchronous processing to prevent timing attacks and email enumeration.
     * Always returns success response regardless of email existence in system.
     *
     * @param request the password reset code request containing email and options
     * @return response indicating code was processed (always success for security)
     */
    CodeSendResponse sendPasswordResetCode(CodeSendRequest request);

    /**
     * Generates and sends a password change token via email.
     * Creates a UUID token, stores it in Redis with email as value for 10 minutes,
     * and sends an email with the password change link.
     *
     * @param request the password change token request containing email
     * @return response with token and expiry information
     */
    PasswordChangeTokenResponse sendPasswordChangeToken(PasswordChangeTokenRequest request);



}