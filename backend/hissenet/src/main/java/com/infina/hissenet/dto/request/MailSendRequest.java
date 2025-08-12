package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MailSendRequest(
        @NotBlank(message = "{validation.email.required}")
        @Email(message = "{validation.email.invalid}")
        String to,
        @NotBlank(message = "{validation.mail.subject.required}") String subject,
        @NotBlank(message = "{validation.mail.content.required}") String content,
        String recipientName
) {}