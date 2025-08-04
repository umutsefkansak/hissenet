package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
        @Email
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z]).*$",
                message = "Password must contain at least one uppercase and one lowercase letter"
        )
        String password,
        String confirmNewPassword

) {
}
