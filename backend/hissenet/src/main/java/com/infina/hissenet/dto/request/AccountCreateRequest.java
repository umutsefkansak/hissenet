package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountCreateRequest(
		
		@NotBlank(message = "{validation.username.required}")
        @Size(min = 3, max = 50, message = "{validation.username.size}")
        String username,

        @NotBlank(message = "{validation.password.required}")
        @Size(min = 8, max = 60, message = "{validation.password.size}")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "{validation.password.pattern}"
        )
        String passwordHash,

        @NotNull(message = "{validation.employee.id.required}")
        Long employeeId
        
) {

}
