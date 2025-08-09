package com.infina.hissenet.dto.request;
import com.infina.hissenet.validation.UniqueValue;
import com.infina.hissenet.validation.UniqueValueType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CorporateCustomerCreateRequest(
        @UniqueValue(type = UniqueValueType.CUSTOMER_EMAIL)
        @Email(message = "{validation.email.invalid}")
        @NotBlank(message = "{validation.email.required}") String email,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "{validation.phone.invalid}") String phone,
        String nationality,
        @NotBlank(message = "{validation.company.name.required}") String companyName,
        @UniqueValue(type = UniqueValueType.TAX_NUMBER)
        @Pattern(regexp = "^[0-9]{10}$", message = "{validation.tax.number.invalid}") String taxNumber,

        String tradeRegistryNumber,
        LocalDate establishmentDate,
        String sector,
        String authorizedPersonName,
        String authorizedPersonTitle,
        String website,
        @DecimalMin(value = "0.0", message = "{validation.commission.negative}")
        BigDecimal commissionRate,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "{validation.phone.invalid}") String authorizedPersonPhone,
        @Pattern(regexp = "^[1-9][0-9]{10}$", message = "{validation.tc.number.invalid}") String authorizedPersonTcNumber,
        @Email(message = "{validation.email.invalid}") String authorizedPersonEmail,
        String taxOffice

) {}