package com.infina.hissenet.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record CodeSendRequest(
        @NotBlank @Email String email,
        String recipientName,
        String description,
        @Min(1) @Max(10) Integer maxAttempts,
        @Min(1) @Max(60) Integer expiryMinutes,
        String additionalInfo
) {}