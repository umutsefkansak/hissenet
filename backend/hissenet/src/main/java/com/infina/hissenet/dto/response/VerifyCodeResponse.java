package com.infina.hissenet.dto.response;

import java.time.LocalDateTime;

public record VerifyCodeResponse(
        boolean valid,
        String message,
        Integer remainingAttempts,
        boolean blocked,
        LocalDateTime blockedUntil
) {
    // Success response
    public static VerifyCodeResponse success(String message) {
        return new VerifyCodeResponse(true, message, null, false, null);
    }

    // Failed attempt response
    public static VerifyCodeResponse failedAttempt(String message, int remainingAttempts) {
        return new VerifyCodeResponse(false, message, remainingAttempts, false, null);
    }

    // Blocked response
    public static VerifyCodeResponse blocked(String message, LocalDateTime blockedUntil) {
        return new VerifyCodeResponse(false, message, 0, true, blockedUntil);
    }

    // Generic failure
    public static VerifyCodeResponse failure(String message) {
        return new VerifyCodeResponse(false, message, null, false, null);
    }
}