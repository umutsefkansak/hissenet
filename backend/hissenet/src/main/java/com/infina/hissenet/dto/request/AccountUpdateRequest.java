package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest(
		 
		@NotBlank(message = "Kullanıcı adı boş olamaz.")
	    @Size(min = 3, max = 50, message = "Kullanıcı adı 3 ile 50 karakter arasında olmalıdır.")
	    String username,

	    @NotNull(message = "Çalışan ID'si belirtilmelidir.")
	    Long employeeId
	    
) {

}
