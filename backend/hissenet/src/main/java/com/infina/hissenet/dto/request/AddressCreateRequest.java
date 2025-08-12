package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AddressCreateRequest(
        @NotNull(message = "{validation.address.type.required}") AddressType addressType,
        @NotBlank(message = "{validation.address.street.required}") String street,
        String district,
        @NotBlank(message = "{validation.address.city.required}") String city,
        @NotBlank(message = "{validation.address.state.required}") String state,
        @NotBlank(message = "{validation.address.country.required}") String country,
        @Pattern(regexp = "^[0-9]{5}$", message = "{validation.address.postal.code.pattern}") String postalCode,
        Boolean isPrimary,
        @NotNull(message = "{validation.customer.id.required}") Long customerId
) {}