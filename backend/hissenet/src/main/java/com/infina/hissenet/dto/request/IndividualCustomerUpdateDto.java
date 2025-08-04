package com.infina.hissenet.dto.request;


import com.infina.hissenet.entity.enums.Gender;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.validation.UniqueValue;
import com.infina.hissenet.validation.UniqueValueType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record IndividualCustomerUpdateDto(
        @UniqueValue(type = UniqueValueType.CUSTOMER_EMAIL)
        @Email String email,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phone,
        String nationality,
        String firstName,
        String middleName,
        String lastName,
        @UniqueValue(type = UniqueValueType.TC_NUMBER)
        @Pattern(regexp = "^[1-9][0-9]{10}$") String tcNumber,
        LocalDate birthDate,
        String birthPlace,
        Gender gender,
        String motherName,
        String fatherName,
        String profession,
        String educationLevel,
        RiskProfile riskProfile
) {}