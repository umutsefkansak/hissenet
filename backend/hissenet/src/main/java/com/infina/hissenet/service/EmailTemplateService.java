package com.infina.hissenet.service;


import com.infina.hissenet.entity.enums.EmailType;
import com.infina.hissenet.service.abstracts.IEmailTemplateService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailTemplateService implements IEmailTemplateService {

    private final TemplateEngine templateEngine;

    public EmailTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    public String renderVerificationCodeTemplate(String recipientName, String code,
                                                 EmailType type, String additionalInfo,
                                                 int maxAttempts, int expiryMinutes) {
        Context context = new Context();
        context.setVariable("recipientName", recipientName);
        context.setVariable("code", code);
        context.setVariable("emailType", type);
        context.setVariable("additionalInfo", additionalInfo);
        context.setVariable("maxAttempts", maxAttempts);
        context.setVariable("expiryMinutes", expiryMinutes);
        context.setVariable("purpose", getPurposeMessageForType(type));

        return templateEngine.process("email/verification-code", context);
    }


    public String renderNotificationTemplate(String recipientName, EmailType type, String message) {
        Context context = new Context();
        context.setVariable("recipientName", recipientName);
        context.setVariable("emailType", type);
        context.setVariable("message", message);

        return templateEngine.process("email/notification", context);
    }


    public String renderGeneralTemplate(String recipientName, String content, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariable("recipientName", recipientName);
        context.setVariable("content", content);

        if (variables != null) {
            variables.forEach(context::setVariable);
        }

        return templateEngine.process("email/general", context);
    }

    private String getPurposeMessageForType(EmailType type) {
        return switch (type) {
            case PASSWORD_RESET -> "Şifre sıfırlama işleminiz";
            case LOGIN_NOTIFICATION -> "Giriş işleminiz";
            case VERIFICATION_CODE -> "Hesap doğrulama işleminiz";
            case TRADE_NOTIFICATION -> "İşlem bildiriminiz";
            case ACCOUNT_STATUS -> "Hesap durumu güncellemesi";
            default -> "Güvenlik işleminiz";
        };
    }
}