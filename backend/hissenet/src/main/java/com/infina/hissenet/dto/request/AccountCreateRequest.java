package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountCreateRequest(
		
		@NotBlank(message = "Kullanıcı adı boş olamaz.")
        @Size(min = 3, max = 50, message = "Kullanıcı adı 3 ile 50 karakter arasında olmalıdır.")
        String username,

        @NotBlank(message = "Şifre boş olamaz.")
        @Size(min = 8, max = 60, message = "Şifre en az 8, en fazla 60 karakter olmalıdır.")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Şifre en az bir büyük harf, bir küçük harf ve bir rakam içermelidir."
        )
        String passwordHash,

        @NotNull(message = "Çalışan ID'si belirtilmelidir.")
        Long employeeId
        
) {

}
