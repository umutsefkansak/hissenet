package com.infina.hissenet.dto.common;

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
        String educationLevel
) implements CustomerDto {}