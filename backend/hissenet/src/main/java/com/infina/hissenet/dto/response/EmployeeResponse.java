package com.infina.hissenet.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.infina.hissenet.entity.enums.EmployeeStatus;

public record EmployeeResponse(
		Long id,
	    String firstName,
	    String lastName,
	    String email,
	    String phone,
	    String position,
	    LocalDate hireDate,
	    LocalDate terminationDate,
	    EmployeeStatus status,
	    String emergencyContactName,
	    String emergencyContactPhone,
	    Long accountId,
	    Set<Long> roleIds,
	    LocalDateTime createdAt,
	    LocalDateTime updatedAt
) {

}
