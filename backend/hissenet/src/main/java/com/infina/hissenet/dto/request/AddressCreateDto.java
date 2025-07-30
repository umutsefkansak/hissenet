package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AddressCreateDto(
        @NotNull AddressType addressType,
        @NotBlank String street,
        String district,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String country,
        @Pattern(regexp = "^[0-9]{5}$") String postalCode,
        Boolean isPrimary,
        @NotNull Long customerId
) {}