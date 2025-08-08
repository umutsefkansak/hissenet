package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest(
		 
		@NotBlank(message = "{validation.username.required}")
	    @Size(min = 3, max = 50, message = "{validation.username.size}")
	    String username,

	    @NotNull(message = "{validation.employee.id.required}")
	    Long employeeId
	    
) {

}
