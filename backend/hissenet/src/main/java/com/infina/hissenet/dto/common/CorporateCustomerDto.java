package com.infina.hissenet.dto.common;


import java.math.BigDecimal;
import java.time.LocalDate;

public record CorporateCustomerDto(
        Long id,
        String customerNumber,
        String email,
        String phone,
        String nationality,
        Boolean kycVerified,
        String customerType,
        String companyName,
        String taxNumber,
        String tradeRegistryNumber,
        LocalDate establishmentDate,
        String sector,
        String authorizedPersonName,
        String authorizedPersonTitle,
        String website,
        BigDecimal commissionRate,
        String authorizedPersonPhone,
        String authorizedPersonTcNumber,
        String authorizedPersonEmail,
        String taxOffice
) implements CustomerDto {}