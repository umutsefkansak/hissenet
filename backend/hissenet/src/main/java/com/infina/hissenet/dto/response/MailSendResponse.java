package com.infina.hissenet.dto.response;
import java.time.LocalDateTime;

public record MailSendResponse(
        boolean success,
        String message,
        String verificationCode,
        LocalDateTime sentAt,
        Integer maxAttempts,
        Integer expiryMinutes
) {
    public static MailSendResponse success(String message) {
        return new MailSendResponse(true, message, null, LocalDateTime.now(), null, null);
    }

    public static MailSendResponse successWithCode(String message, String code, int maxAttempts, int expiryMinutes) {
        return new MailSendResponse(true, message, code, LocalDateTime.now(), maxAttempts, expiryMinutes);
    }

    public static MailSendResponse failure(String message) {
        return new MailSendResponse(false, message, null, LocalDateTime.now(), null, null);
    }
}