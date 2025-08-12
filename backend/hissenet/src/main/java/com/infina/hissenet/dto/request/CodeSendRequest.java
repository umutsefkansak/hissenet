package com.infina.hissenet.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record CodeSendRequest(
        @NotBlank(message = "{validation.email.required}")
        @Email(message = "{validation.email.invalid}") String email,
        String recipientName,
        String description,
        @Min(value = 1, message = "{validation.max.attempts.min}")
        @Max(value = 10, message = "{validation.max.attempts.max}") Integer maxAttempts,

        @Min(value = 1, message = "{validation.expiry.minutes.min}")
        @Max(value = 60, message = "{validation.expiry.minutes.max}") Integer expiryMinutes,
        String additionalInfo
) {}