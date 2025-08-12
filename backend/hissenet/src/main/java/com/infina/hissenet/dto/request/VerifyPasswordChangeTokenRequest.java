package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyPasswordChangeTokenRequest(
        @NotBlank(message = "{validation.token.required}")
        String token
) {} 