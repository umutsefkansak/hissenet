package com.infina.hissenet.dto.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployeeUpdateRequest(

		@NotNull(message = "{validation.employee.id.required}")
	    Long id,

		@Size(min = 2, max = 50, message = "{validation.first.name.size}")
	    String firstName,

		@Size(min = 2, max = 50, message = "{validation.last.name.size}")
	    String lastName,

		@Email(message = "{validation.email.invalid}")
	    String email,

		@Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "{validation.phone.invalid}")
	    String phone,

		@Size(max = 100, message = "{validation.position.size}")
	    String position,

		@Size(max = 100, message = "{validation.emergency.contact.name.size}")
	    String emergencyContactName,

		@Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "{validation.emergency.contact.phone.invalid}")
	    String emergencyContactPhone,

	    Set<Long> roleIds,
		Long updatedByEmployeeId


) {

}
