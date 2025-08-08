package com.infina.hissenet.service;

import com.infina.hissenet.service.abstracts.IMessageService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageService implements IMessageService {

    private final MessageSource messageSource;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public String getMessage(String key) {
        return getMessage(key, (Object[]) null);
    }


    public String getMessage(String key, Object... args) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            return messageSource.getMessage(key, args, locale);
        } catch (Exception e) {
            return key;
        }
    }


    public String getMessage(String key, Locale locale, Object... args) {
        try {
            return messageSource.getMessage(key, args, locale);
        } catch (Exception e) {
            return key;
        }
    }

    public String getMessageTr(String key, Object... args) {
        return getMessage(key, new Locale("tr"), args);
    }
}
