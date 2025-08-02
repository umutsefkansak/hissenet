package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.Gender;
import com.infina.hissenet.validation.UniqueValue;
import com.infina.hissenet.validation.UniqueValueType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record IndividualCustomerCreateDto(
        @UniqueValue(type = UniqueValueType.EMAIL)
        @Email @NotBlank String email,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phone,
        String nationality,
        @NotBlank String firstName,
        String middleName,
        @NotBlank String lastName,
        @UniqueValue(type = UniqueValueType.TC_NUMBER)
        @Pattern(regexp = "^[1-9][0-9]{10}$") String tcNumber,
        LocalDate birthDate,
        String birthPlace,
        Gender gender,
        String motherName,
        String fatherName,
        String profession,
        String educationLevel
) {}