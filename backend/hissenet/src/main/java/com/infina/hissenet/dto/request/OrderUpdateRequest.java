package com.infina.hissenet.dto.request;

import java.math.BigDecimal;

import com.infina.hissenet.entity.enums.OrderStatus;

public record OrderUpdateRequest(
		Long id,
		BigDecimal quantity,
		BigDecimal price,
		OrderStatus status
) {

}
