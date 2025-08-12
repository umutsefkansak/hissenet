package com.infina.hissenet.dto.response;

public record MailSendResponse(
        boolean success,
        String message
) {
    public static MailSendResponse success(String message) {
        return new MailSendResponse(true, message);
    }

    public static MailSendResponse failure(String message) {
        return new MailSendResponse(false, message);
    }
}