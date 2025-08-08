package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountPasswordChangeRequest(
		
		@NotBlank(message = "{validation.new.password.required}")
	    @Size(min = 8, max = 60, message = "{validation.new.password.size}")
	    @Pattern(
	        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
	        message = "{validation.password.pattern}"
	    )
	    String newPassword,

	    @NotBlank(message = "{validation.confirm.password.required}")
	    String confirmNewPassword
	    
) {

}
