package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.EmailType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerificationCodeSendDto(
        @NotBlank @Email String to,
        @NotNull EmailType type,
        String recipientName,
        String additionalInfo
) {}