package com.infina.hissenet.dto.response;

public record PasswordChangeTokenResponse(
        String message
) {
    public static PasswordChangeTokenResponse success(String message) {
        return new PasswordChangeTokenResponse(message);
    }
} 