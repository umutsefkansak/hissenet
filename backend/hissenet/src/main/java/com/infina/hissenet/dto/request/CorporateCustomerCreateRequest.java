package com.infina.hissenet.dto.request;
import com.infina.hissenet.validation.UniqueValue;
import com.infina.hissenet.validation.UniqueValueType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CorporateCustomerCreateRequest(
        @UniqueValue(type = UniqueValueType.CUSTOMER_EMAIL)
        @Email @NotBlank String email,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phone,
        String nationality,
        @NotBlank String companyName,
        @UniqueValue(type = UniqueValueType.TAX_NUMBER)
        @Pattern(regexp = "^[0-9]{10}$") String taxNumber,
        String tradeRegistryNumber,
        LocalDate establishmentDate,
        String sector,
        String authorizedPersonName,
        String authorizedPersonTitle,
        String website,
        BigDecimal commissionRate,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$") String authorizedPersonPhone,
        @Pattern(regexp = "^[1-9][0-9]{10}$") String authorizedPersonTcNumber,
        @Email String authorizedPersonEmail,
        String taxOffice

) {}