package com.infina.hissenet.dto.common;

import com.infina.hissenet.entity.enums.IncomeRange;
import com.infina.hissenet.entity.enums.RiskProfile;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndividualCustomerDto(
        Long id,
        String customerNumber,
        String email,
        String phone,
        String nationality,
        Boolean kycVerified,
        String customerType,
        String firstName,
        String middleName,
        String lastName,
        String tcNumber,
        LocalDate birthDate,
        String birthPlace,
        String gender,
        String motherName,
        String fatherName,
        String profession,
        String educationLevel,
        RiskProfile riskProfile,
        BigDecimal commissionRate,
        IncomeRange incomeRange
) implements CustomerDto {}