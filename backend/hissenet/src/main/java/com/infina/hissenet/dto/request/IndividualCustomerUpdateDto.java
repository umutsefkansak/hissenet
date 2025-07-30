package com.infina.hissenet.dto.request;


import com.infina.hissenet.entity.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record IndividualCustomerUpdateDto(
        @Email String email,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phone,
        String nationality,
        String firstName,
        String middleName,
        String lastName,
        @Pattern(regexp = "^[1-9][0-9]{10}$") String tcNumber,
        LocalDate birthDate,
        String birthPlace,
        Gender gender,
        String motherName,
        String fatherName,
        String profession,
        String educationLevel
) {}