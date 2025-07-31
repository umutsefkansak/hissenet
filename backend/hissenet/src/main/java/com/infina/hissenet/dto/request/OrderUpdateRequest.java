package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record OrderUpdateRequest(
		
		@NotNull(message = "Emir durumu boş olamaz")
        OrderStatus status
        
) {

}
