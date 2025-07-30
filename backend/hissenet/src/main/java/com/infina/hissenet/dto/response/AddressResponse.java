package com.infina.hissenet.dto.response;

import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.entity.enums.AddressType;

public record AddressResponse(
        Long id,
        AddressType addressType,
        String street,
        String district,
        String city,
        String state,
        String country,
        String postalCode,
        Boolean isPrimary,
        CustomerDto customer,
        String fullAddress
) {}