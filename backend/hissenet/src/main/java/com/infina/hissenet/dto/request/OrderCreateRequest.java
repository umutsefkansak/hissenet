package com.infina.hissenet.dto.request;

import java.math.BigDecimal;

import com.infina.hissenet.entity.enums.OrderCategory;
import com.infina.hissenet.entity.enums.OrderType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(
		
		@NotNull(message = "Müşteri ID'si boş olamaz")
        Long customerId,

        @NotNull(message = "Hisse ID'si boş olamaz")
        Long stockId,

        @NotNull(message = "Emir türü (category) boş olamaz")
        OrderCategory category,

        @NotNull(message = "İşlem tipi (type) boş olamaz")
        OrderType type,

        @NotNull(message = "Adet bilgisi boş olamaz")
        @DecimalMin(value = "0.0001", message = "Adet en az 0.0001 olmalıdır")
        BigDecimal quantity,

        @DecimalMin(value = "0.0001", inclusive = false, message = "Fiyat pozitif olmalıdır")
        BigDecimal price
        
) {

}
