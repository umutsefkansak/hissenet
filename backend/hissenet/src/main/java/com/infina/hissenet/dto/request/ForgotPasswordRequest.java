package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
        @Email(message = "{validation.email.invalid}")
        String email,

        @NotBlank(message = "{validation.password.required}")
        @Size(min = 8, max = 64, message = "{validation.employee.password.size}")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z]).*$",
                message = "{validation.employee.password.pattern}"
        )
        String password,
        String confirmNewPassword

) {
}
