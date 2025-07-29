package com.infina.hissenet.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.infina.hissenet.entity.enums.OrderCategory;
import com.infina.hissenet.entity.enums.OrderStatus;
import com.infina.hissenet.entity.enums.OrderType;

public record OrderResponse(
		Long id,
	    Long customerId,
	    Long stockId,
	    OrderCategory category,
	    OrderType type,
	    OrderStatus status,
	    BigDecimal quantity,
	    BigDecimal price,
	    BigDecimal totalAmount,
	    LocalDateTime createdAt,
	    LocalDateTime updatedAt,
	    Long createdById,
	    Long updatedById
) {

}
