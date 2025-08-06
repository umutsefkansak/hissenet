package com.infina.hissenet.dto.request;

import java.util.Set;

import com.infina.hissenet.validation.UniqueValue;
import com.infina.hissenet.validation.UniqueValueType;
import jakarta.validation.constraints.*;


public record EmployeeCreateRequest(

		@NotBlank(message = "First name is required")
		@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
		String firstName,

		@NotBlank(message = "Last name is required")
		@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
		String lastName,

		@NotBlank(message = "Email is required")
		@Email(message = "Invalid email format")
		@UniqueValue(type = UniqueValueType.EMPLOYEE_EMAIL)
		String email,

		@NotBlank(message = "Phone is required")
		@Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "Invalid phone number")
		String phone,

		@NotBlank(message = "Position is required")
		@Size(max = 100, message = "Position must be at most 100 characters")
		String position,

		@NotBlank(message = "Password is required")
		@Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
		@Pattern(
				regexp = "^(?=.*[a-z])(?=.*[A-Z]).*$",
				message = "Password must contain at least one uppercase and one lowercase letter"
		)
		String password,

		@NotBlank(message = "Emergency contact name is required")
		@Size(max = 100, message = "Emergency contact name must be at most 100 characters")
		String emergencyContactName,

		@NotBlank(message = "Emergency contact phone is required")
		@Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "Invalid emergency contact phone number")
		String emergencyContactPhone,

		@NotNull(message = "Role IDs cannot be null")
		@Size(min = 1, message = "At least one role ID must be provided")
		Set<@NotNull(message = "Role ID cannot be null") Long> roleIds,

		Boolean isOnLeave

) {}

