package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.EmailType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TradeNotificationDto(
        @NotBlank @Email String email,
        String recipientName,
        @NotNull EmailType type,
        @NotBlank String message
) {}