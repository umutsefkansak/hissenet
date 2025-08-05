package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.Gender;
import com.infina.hissenet.entity.enums.IncomeRange;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.validation.MinAge;
import com.infina.hissenet.validation.UniqueValue;
import com.infina.hissenet.validation.UniqueValueType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndividualCustomerCreateDto(
        @UniqueValue(type = UniqueValueType.CUSTOMER_EMAIL)
        @Email @NotBlank String email,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phone,
        String nationality,
        @NotBlank String firstName,
        String middleName,
        @NotBlank String lastName,
        @UniqueValue(type = UniqueValueType.TC_NUMBER)
        @Pattern(regexp = "^[1-9][0-9]{10}$") String tcNumber,
        @MinAge(18)
        LocalDate birthDate,
        String birthPlace,
        Gender gender,
        String motherName,
        String fatherName,
        String profession,
        String educationLevel,
        RiskProfile riskProfile,
        @DecimalMin(value = "0.0", message = "Commission rate cannot be negative")
        @DecimalMax(value = "5.0", message = "Commission rate cannot exceed 5%")
        BigDecimal commissionRate,
        IncomeRange incomeRange
) {}