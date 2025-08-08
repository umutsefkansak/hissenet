package com.infina.hissenet.dto.request;

import java.util.Set;

import com.infina.hissenet.validation.UniqueValue;
import com.infina.hissenet.validation.UniqueValueType;
import jakarta.validation.constraints.*;


public record EmployeeCreateRequest(

		@NotBlank(message = "{validation.required}")
		@Size(min = 2, max = 50, message = "{validation.first.name.size}")
		String firstName,

		@NotBlank(message = "{validation.last.name.required}")
		@Size(min = 2, max = 50, message = "{validation.last.name.size}")
		String lastName,

		@NotBlank(message = "{validation.email.required}")
		@Email(message = "{validation.email.invalid}")
		@UniqueValue(type = UniqueValueType.EMPLOYEE_EMAIL)
		String email,

		@NotBlank(message = "{validation.phone.required}")
		@Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "{validation.phone.invalid}")
		String phone,

		@NotBlank(message = "{validation.position.required}")
		@Size(max = 100, message = "{validation.position.size}")
		String position,

		@NotBlank(message = "{validation.password.required}")
		@Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
		@Size(min = 8, max = 64, message = "{validation.employee.password.size}")
		@Pattern(
				regexp = "^(?=.*[a-z])(?=.*[A-Z]).*$",
				message = "{validation.employee.password.pattern}"
		)
		String password,

		@NotBlank(message = "{validation.emergency.contact.name.required}")
		@Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "{validation.emergency.contact.phone.invalid}")
		String emergencyContactName,

		@NotBlank(message = "{validation.emergency.contact.phone.required}")
		@Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "{validation.emergency.contact.phone.invalid}")
		String emergencyContactPhone,

		@NotNull(message = "{validation.role.ids.required}")
		@Size(min = 1, message = "{validation.role.ids.min}")
		Set<@NotNull(message = "{validation.role.id.required}") Long> roleIds

) {}

