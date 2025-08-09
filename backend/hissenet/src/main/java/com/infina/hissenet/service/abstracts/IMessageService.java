package com.infina.hissenet.service.abstracts;

import java.util.Locale;

/**
 * Service interface for internationalization (i18n) message handling.
 * Provides methods to retrieve localized messages from message properties files.
 *
 * @author Umut Sefkan Sak
 * @version 1.0
 * @since 1.0
 */
public interface IMessageService {

    /**
     * Retrieves a localized message by key using the current locale context.
     *
     * @param key the message key from properties file
     * @return the localized message or key if not found
     */
    String getMessage(String key);

    /**
     * Retrieves a localized message by key with parameters using the current locale context.
     *
     * @param key the message key from properties file
     * @param args optional parameters for message formatting
     * @return the localized message or key if not found
     */
    String getMessage(String key, Object... args);

    /**
     * Retrieves a localized message for a specific locale with parameters.
     *
     * @param key the message key from properties file
     * @param locale the target locale for localization
     * @param args optional parameters for message formatting
     * @return the localized message or key if not found
     */
    String getMessage(String key, Locale locale, Object... args);

    /**
     * Retrieves a Turkish localized message by key with optional parameters.
     * This is a convenience method that forces Turkish locale.
     *
     * @param key the message key from properties file
     * @param args optional parameters for message formatting
     * @return the Turkish localized message or key if not found
     */
    String getMessageTr(String key, Object... args);
}