package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountPasswordChangeRequest(
		
		@NotBlank(message = "Yeni şifre boş olamaz.")
	    @Size(min = 8, max = 60, message = "Yeni şifre en az 8, en fazla 60 karakter olmalıdır.")
	    @Pattern(
	        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
	        message = "Şifre en az bir büyük harf, bir küçük harf ve bir rakam içermelidir."
	    )
	    String newPassword,

	    @NotBlank(message = "Şifre onayı boş olamaz.")
	    String confirmNewPassword
	    
) {

}
