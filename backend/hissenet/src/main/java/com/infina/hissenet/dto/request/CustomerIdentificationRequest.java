package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerIdentificationRequest(
        @NotBlank(message = "{validation.identification.number.required}")
        @Size(min = 10, max = 11, message = "{validation.identification.number.size}") String identificationNumber
) {}