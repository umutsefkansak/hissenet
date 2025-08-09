package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank(message = "{validation.email.required}")
        @Email(message = "{validation.email.invalid}")
        String email,
        @NotBlank(message = "{validation.password.required}")
        @Size(min = 6, max = 100, message = "{validation.login.password.size}")
        String password
) {
}
