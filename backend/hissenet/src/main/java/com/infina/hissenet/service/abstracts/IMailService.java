package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.entity.VerificationCode;
import com.infina.hissenet.entity.enums.EmailType;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Main service interface for mail operations.
 * This interface defines all mail operations including mail sending, verification codes,
 * notifications, and security controls.
 *
 * <p>The interface supports mail sending at different security levels,
 * verification code management, IP and email-based limit controls.</p>
 *
 * <p>Works integrated with async mail sending and security controls.</p>
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 * @since 1.0
 */
public interface IMailService {

    /**
     * General purpose mail sending method.
     *
     * <p>This method works asynchronously and performs mail sending
     * in the background. Success/error states are returned in the response.</p>
     *
     * @param request DTO containing required information for mail sending
     *                (recipient email, subject, content, recipient name)
     * @return {@link MailSendResponse} response containing the result of mail sending operation
     * @throws IllegalArgumentException if request parameter is null
     */
    MailSendResponse sendMail(MailSendDto request);

    /**
     * Sends verification code with default attempt count.
     *
     * <p>This method sends verification code at standard security level.
     * Performs daily limit check and invalidates old codes.</p>
     *
     * @param request DTO containing required information for verification code sending
     * @return {@link MailSendResponse} result of verification code sending operation
     * @see #sendVerificationCodeWithAttempts(VerificationCodeSendDto, int)
     */
    MailSendResponse sendVerificationCode(VerificationCodeSendDto request);

    /**
     * Sends verification code with custom attempt count.
     *
     * <p>This method creates verification code with specified maximum attempt count.
     * Performs daily limit check, blocked code check, and invalidates old codes
     * after creating new code.</p>
     *
     * <p>Security controls:</p>
     * <ul>
     *   <li>Daily code sending limit check</li>
     *   <li>Blocked code check</li>
     *   <li>Invalidating old codes</li>
     * </ul>
     *
     * @param request DTO containing verification code information
     * @param maxAttempts maximum number of wrong attempts
     * @return {@link MailSendResponse} result of code sending operation and code information
     * @throws IllegalArgumentException if maxAttempts is less than 0 or greater than 10
     */
    MailSendResponse sendVerificationCodeWithAttempts(VerificationCodeSendDto request, int maxAttempts);

    /**
     * Checks and verifies the verification code.
     *
     * <p>This method performs comprehensive security checks:</p>
     * <ul>
     *   <li>IP-based attempt limit check (hourly)</li>
     *   <li>Active code existence check</li>
     *   <li>Code correctness check</li>
     *   <li>Wrong attempt count tracking</li>
     *   <li>Code blocking mechanism</li>
     * </ul>
     *
     * @param dto DTO containing email, code and type information for verification
     * @param request HTTP request object (for IP address detection)
     * @return {@link VerifyCodeResponse} verification result, remaining attempts or block information
     * @throws IllegalArgumentException if dto or request parameter is null
     */
    VerifyCodeResponse verifyCode(VerifyCodeDto dto, HttpServletRequest request);

    /**
     * Sends verification code for high security operations.
     *
     * <p>Sends code with fewer attempt rights for critical operations
     * (password reset, account deletion, etc.). High security level.</p>
     *
     * @param request DTO containing verification code information
     * @return {@link MailSendResponse} high security code sending result
     */
    MailSendResponse sendHighSecurityCode(VerificationCodeSendDto request);

    /**
     * Sends verification code at standard security level.
     *
     * <p>Sends code with medium level security for normal operations.
     * Has appropriate attempt count for most operations.</p>
     *
     * @param request DTO containing verification code information
     * @return {@link MailSendResponse} standard code sending result
     */
    MailSendResponse sendStandardCode(VerificationCodeSendDto request);

    /**
     * Sends flexible verification code for low security operations.
     *
     * <p>Sends code with more attempt rights for non-critical operations.
     * Prioritizes user experience.</p>
     *
     * @param request DTO containing verification code information
     * @return {@link MailSendResponse} low security code sending result
     */
    MailSendResponse sendLowSecurityCode(VerificationCodeSendDto request);

    /**
     * Marks verification code as used.
     *
     * <p>After successful verification, marks the code as used
     * and records IP address. This code cannot be used again.</p>
     *
     * @param code verification code entity to be marked as used
     * @param ipAddress IP address of the user performing the operation
     * @throws IllegalArgumentException if code parameter is null
     */
    void markAsUsed(VerificationCode code, String ipAddress);

    /**
     * Sends quick notification mail.
     *
     * <p>This method works asynchronously and is used for system notifications,
     * alerts or informational messages. Creates template-based content.</p>
     *
     * @param email recipient's email address
     * @param recipientName recipient's name
     * @param type notification type (LOGIN_NOTIFICATION, TRADE_NOTIFICATION, etc.)
     * @param message notification message
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    void sendNotification(String email, String recipientName, EmailType type, String message);

    /**
     * Sends login verification code.
     *
     * <p>Sends verification code for user login.
     * Works at standard security level.</p>
     *
     * @param dto DTO containing email and recipient name information for login code
     * @return {@link MailSendResponse} login code sending result
     */
    MailSendResponse sendLoginCode(LoginCodeDto dto);

    /**
     * Sends password reset verification code.
     *
     * <p>Sends high security verification code for password reset operation.
     * Provides critical security with fewer attempt rights.</p>
     *
     * @param dto DTO containing email and recipient name information for password reset
     * @return {@link MailSendResponse} password reset code sending result
     */
    MailSendResponse sendPasswordResetCode(PasswordResetCodeDto dto);

    /**
     * Cleans up expired verification codes.
     *
     * <p>This method can be run as a scheduled task.
     * Deletes old codes taking up space in database and improves performance.</p>
     *
     * <p>Cleanup criteria: Codes older than specified number of days are deleted.</p>
     */
    void cleanupExpiredCodes();

    /**
     * Unblocks codes whose block period has expired.
     *
     * <p>Removes block status of blocked codes older than specified
     * number of hours. This allows users to request verification
     * codes again.</p>
     */
    void unblockExpiredCodes();

    /**
     * Checks if email address has exceeded daily limit.
     *
     * <p>Checks the number of codes sent to specified email address
     * in the last 24 hours and returns limit status.</p>
     *
     * @param email email address to check
     * @return {@code true} if limit is exceeded, {@code false} if not exceeded
     * @throws IllegalArgumentException if email parameter is null or empty
     */
    boolean isEmailLimitExceeded(String email);

    /**
     * Checks if IP address has exceeded hourly attempt limit.
     *
     * <p>Checks the number of verification attempts made from specified
     * IP address in the last 1 hour. Provides protection against
     * brute force attacks.</p>
     *
     * @param ipAddress IP address to check
     * @return {@code true} if limit is exceeded, {@code false} if not exceeded
     * @throws IllegalArgumentException if ipAddress parameter is null or empty
     */
    boolean isIpLimitExceeded(String ipAddress);
}