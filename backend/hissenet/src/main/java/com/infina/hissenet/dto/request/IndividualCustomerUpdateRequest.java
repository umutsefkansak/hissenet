package com.infina.hissenet.dto.request;


import com.infina.hissenet.entity.enums.Gender;
import com.infina.hissenet.entity.enums.IncomeRange;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.validation.MinAge;
import com.infina.hissenet.validation.UniqueValue;
import com.infina.hissenet.validation.UniqueValueType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndividualCustomerUpdateRequest(
        @UniqueValue(type = UniqueValueType.CUSTOMER_EMAIL)
        @Email(message = "{validation.email.invalid}")
        String email,

        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "{validation.phone.invalid}")
        String phone,
        String nationality,
        String firstName,
        String middleName,
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
        BigDecimal commissionRate,
        IncomeRange incomeRange,
        Long updatedByEmployeeId

) {}