package com.infina.hissenet.dto.request;


import com.infina.hissenet.entity.enums.EmailType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VerifyCodeDto(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 4, max = 10) String code,
        @NotNull EmailType type
) {}