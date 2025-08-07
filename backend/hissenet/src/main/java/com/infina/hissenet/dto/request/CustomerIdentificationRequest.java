package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerIdentificationRequest(
        @NotBlank @Size(min = 10, max = 11) String identificationNumber
) {}