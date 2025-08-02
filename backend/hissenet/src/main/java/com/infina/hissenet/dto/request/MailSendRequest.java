package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MailSendRequest(
        @NotBlank @Email String to,
        @NotBlank String subject,
        @NotBlank String content,
        String recipientName
) {}