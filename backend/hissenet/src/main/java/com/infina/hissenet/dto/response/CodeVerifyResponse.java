package com.infina.hissenet.dto.response;


import java.time.LocalDateTime;

public record CodeVerifyResponse(
        boolean success,
        String message,
        int remainingAttempts,
        boolean blocked,
        LocalDateTime blockedUntil
) {
    public static CodeVerifyResponse success(String message) {
        return new CodeVerifyResponse(true, message, 0, false, null);
    }

    public static CodeVerifyResponse failure(String message, int remainingAttempts) {
        return new CodeVerifyResponse(false, message, remainingAttempts, false, null);
    }

    public static CodeVerifyResponse blocked(String message, LocalDateTime blockedUntil) {
        return new CodeVerifyResponse(false, message, 0, true, blockedUntil);
    }
}