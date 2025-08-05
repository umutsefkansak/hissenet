package com.infina.hissenet.dto.response;

public record VerifyPasswordChangeTokenResponse(
        String message,
        String email,
        boolean valid
) {
    public static VerifyPasswordChangeTokenResponse success(String message, String email) {
        return new VerifyPasswordChangeTokenResponse(message, email, true);
    }
    
    public static VerifyPasswordChangeTokenResponse failure(String message) {
        return new VerifyPasswordChangeTokenResponse(message, null, false);
    }
} 