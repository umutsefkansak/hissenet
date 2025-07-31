package com.infina.hissenet.service.abstracts;


import com.infina.hissenet.entity.enums.EmailType;

import java.util.Map;

/**
 * Service interface for email template rendering operations.
 * This interface provides methods for rendering different types of email templates
 * using Thymeleaf template engine with dynamic content and variables.
 *
 * <p>The interface supports various email template types including verification codes,
 * notifications, and general purpose templates with custom variables.</p>
 *
 * <p>All templates are processed with Thymeleaf context and return HTML content
 * ready for email sending.</p>
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 * @since 1.0
 */
public interface IEmailTemplateService {

    /**
     * Renders verification code email template.
     *
     * <p>This method creates a formatted email template for verification codes
     * with all necessary information including recipient details, code, expiry time,
     * and attempt limits. The template includes security information and usage instructions.</p>
     *
     * <p>Template variables include:</p>
     * <ul>
     *   <li>recipientName - Name of the email recipient</li>
     *   <li>code - Verification code to be displayed</li>
     *   <li>emailType - Type of email for contextual messaging</li>
     *   <li>additionalInfo - Extra information specific to the operation</li>
     *   <li>maxAttempts - Maximum number of attempts allowed</li>
     *   <li>expiryMinutes - Code expiration time in minutes</li>
     *   <li>purpose - Localized purpose message based on email type</li>
     * </ul>
     *
     * @param recipientName name of the person receiving the email
     * @param code verification code to include in the template
     * @param type email type that determines the context and messaging
     * @param additionalInfo additional context-specific information
     * @param maxAttempts maximum number of verification attempts allowed
     * @param expiryMinutes number of minutes until code expires
     * @return rendered HTML email template as String
     * @throws IllegalArgumentException if recipientName or code is null or empty
     * @throws org.thymeleaf.exceptions.TemplateInputException if template processing fails
     */
    String renderVerificationCodeTemplate(String recipientName, String code,
                                          EmailType type, String additionalInfo,
                                          int maxAttempts, int expiryMinutes);

    /**
     * Renders notification email template.
     *
     * <p>This method creates email templates for various notification types
     * such as login notifications, trade alerts, account status updates, etc.
     * The template is optimized for informational messages and alerts.</p>
     *
     * <p>Template variables include:</p>
     * <ul>
     *   <li>recipientName - Name of the email recipient</li>
     *   <li>emailType - Type of notification for styling and context</li>
     *   <li>message - Main notification message content</li>
     * </ul>
     *
     * @param recipientName name of the person receiving the notification
     * @param type type of notification (LOGIN_NOTIFICATION, TRADE_NOTIFICATION, etc.)
     * @param message main notification message to be displayed
     * @return rendered HTML notification email template as String
     * @throws IllegalArgumentException if recipientName or message is null or empty
     * @throws org.thymeleaf.exceptions.TemplateInputException if template processing fails
     */
    String renderNotificationTemplate(String recipientName, EmailType type, String message);

    /**
     * Renders general purpose email template with custom variables.
     *
     * <p>This method provides flexibility for creating custom email templates
     * with dynamic content and variables. It's suitable for templates that don't
     * fit into specific categories but need custom variable injection.</p>
     *
     * <p>Base template variables:</p>
     * <ul>
     *   <li>recipientName - Name of the email recipient</li>
     *   <li>content - Main content of the email</li>
     *   <li>[custom variables] - Any additional variables from the variables map</li>
     * </ul>
     *
     * @param recipientName name of the person receiving the email
     * @param content main content/body of the email
     * @param variables optional map of additional template variables, can be null
     * @return rendered HTML email template as String
     * @throws IllegalArgumentException if recipientName or content is null or empty
     * @throws org.thymeleaf.exceptions.TemplateInputException if template processing fails
     */
    String renderGeneralTemplate(String recipientName, String content, Map<String, Object> variables);
}