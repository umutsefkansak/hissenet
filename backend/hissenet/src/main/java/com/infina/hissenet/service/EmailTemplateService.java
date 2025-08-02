package com.infina.hissenet.service;

import com.infina.hissenet.service.abstracts.IEmailTemplateService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailTemplateService implements IEmailTemplateService{

    private final TemplateEngine templateEngine;

    public EmailTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String renderVerificationTemplate(String recipientName, String code, String description,
                                             String additionalInfo, int maxAttempts, int expiryMinutes) {
        Context context = new Context();
        context.setVariable("recipientName", recipientName);
        context.setVariable("code", code);
        context.setVariable("description", description);
        context.setVariable("additionalInfo", additionalInfo);
        context.setVariable("maxAttempts", maxAttempts);
        context.setVariable("expiryMinutes", expiryMinutes);

        return templateEngine.process("email/verification-code", context);
    }

    public String renderNotificationTemplate(String recipientName, String message) {
        Context context = new Context();
        context.setVariable("recipientName", recipientName);
        context.setVariable("message", message);

        return templateEngine.process("email/notification", context);
    }
}