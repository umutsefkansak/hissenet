package com.infina.hissenet.dto.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployeeUpdateRequest(
		
		@NotNull(message = "Employee ID is required")
	    Long id,
	    
	    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	    String firstName,

	    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	    String lastName,

	    @Email(message = "Invalid email format")
	    String email,

	    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "Invalid phone number")
	    String phone,

	    @Size(max = 100, message = "Position must be at most 100 characters")
	    String position,

	    @Size(max = 100, message = "Emergency contact name must be at most 100 characters")
	    String emergencyContactName,

	    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "Invalid emergency contact phone number")
	    String emergencyContactPhone,

	    Set<@NotNull(message = "Role ID cannot be null") Long> roleIds
	    
) {

}
