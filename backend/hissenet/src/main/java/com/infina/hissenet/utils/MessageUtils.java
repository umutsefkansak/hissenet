package com.infina.hissenet.utils;

import com.infina.hissenet.service.MessageService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * Utility class for retrieving localized messages from message properties files.
 * Provides static access to MessageService for consistent i18n message handling.
 *
 * @author Umut Sefkan Sak
 * @version 1.0
 * @since 1.0
 */
@Component
public class MessageUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * Sets the Spring application context.
     *
     * @param applicationContext the Spring application context
     * @throws BeansException if context setting fails
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MessageUtils.applicationContext = applicationContext;
    }


    /**
     * Retrieves a localized Turkish message by key with optional parameters.
     * Falls back to the key itself if message retrieval fails.
     *
     * @param key the message key from properties file
     * @param args optional parameters for message formatting
     * @return the localized message or the key if retrieval fails
     */
    public static String getMessage(String key, Object... args) {
        try {
            if (applicationContext != null) {
                MessageService messageService = applicationContext.getBean(MessageService.class);
                return messageService.getMessageTr(key, args);
            }
        } catch (Exception e) {

        }
        return key;
    }
}
