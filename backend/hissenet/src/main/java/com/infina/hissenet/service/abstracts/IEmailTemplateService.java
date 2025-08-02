package com.infina.hissenet.service.abstracts;

/**
 * Service interface for email template rendering operations.
 * Handles Thymeleaf template processing for different email types.
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 */
public interface IEmailTemplateService {

    /**
     * Renders the verification code email template with provided parameters.
     * Uses Thymeleaf engine to process the template with dynamic content.
     *
     * @param recipientName the name of the email recipient
     * @param code the verification code to be included
     * @param description optional description for the verification purpose
     * @param additionalInfo extra information to include in the template
     * @param maxAttempts maximum number of verification attempts allowed
     * @param expiryMinutes code expiration time in minutes
     * @return rendered HTML content for the verification email
     */
    String renderVerificationTemplate(String recipientName, String code, String description,
                                      String additionalInfo, int maxAttempts, int expiryMinutes);

    /**
     * Renders the notification email template with custom message content.
     * Processes a simple notification template with recipient and message variables.
     *
     * @param recipientName the name of the email recipient
     * @param message the notification message content
     * @return rendered HTML content for the notification email
     */
    String renderNotificationTemplate(String recipientName, String message);
}