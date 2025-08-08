package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CodeVerifyRequest(
        @NotBlank(message = "{validation.email.required}")
        @Email(message = "{validation.email.invalid}") String email,
        @NotBlank(message = "{validation.code.required}")
        @Size(min = 4, max = 10, message = "{validation.code.size}") String code
) {}