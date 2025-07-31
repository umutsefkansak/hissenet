package com.infina.hissenet.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginCodeDto(
        @NotBlank @Email String email,
        String recipientName
) {}