package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.AddressType;
import jakarta.validation.constraints.Pattern;

public record AddressUpdateRequest(
        AddressType addressType,
        String street,
        String district,
        String city,
        String state,
        String country,
        @Pattern(regexp = "^[0-9]{5}$", message = "{validation.address.postal.code.pattern}") String postalCode,
        Boolean isPrimary,
        Long customerId
) {}