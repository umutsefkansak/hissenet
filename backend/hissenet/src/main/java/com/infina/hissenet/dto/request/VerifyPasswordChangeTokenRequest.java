package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyPasswordChangeTokenRequest(
        @NotBlank String token
) {} 