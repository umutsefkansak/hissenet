package com.infina.hissenet.dto.response;


public record CodeSendResponse(
        boolean success,
        String message,

        int maxAttempts,
        int expiryMinutes
) {
    public static CodeSendResponse success(String message, int maxAttempts, int expiryMinutes) {
        return new CodeSendResponse(true, message, maxAttempts, expiryMinutes);
    }
}