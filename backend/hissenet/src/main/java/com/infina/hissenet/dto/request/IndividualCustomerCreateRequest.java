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

public record IndividualCustomerCreateRequest(
        @UniqueValue(type = UniqueValueType.CUSTOMER_EMAIL)
        @Email(message = "{validation.email.invalid}")
        @NotBlank(message = "{validation.email.required}")
        String email,

        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "{validation.phone.invalid}")
        String phone,

        String nationality,
        @NotBlank(message = "{validation.first.name.required}")
        String firstName,

        String middleName,

        @NotBlank(message = "{validation.last.name.required}")
        String lastName,

        @UniqueValue(type = UniqueValueType.TC_NUMBER)
        @Pattern(regexp = "^[1-9][0-9]{10}$", message = "{validation.tc.number.invalid}")
        String tcNumber,
        @MinAge(18)
        LocalDate birthDate,
        String birthPlace,
        Gender gender,
        String motherName,
        String fatherName,
        String profession,
        String educationLevel,
        RiskProfile riskProfile,
        @DecimalMin(value = "0.0", message = "{validation.commission.negative}")
        @DecimalMax(value = "5.0", message = "{validation.commission.max}")
        BigDecimal commissionRate,
        IncomeRange incomeRange
) {}