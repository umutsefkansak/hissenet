package com.infina.hissenet.dto.request;

import java.math.BigDecimal;

import com.infina.hissenet.entity.enums.OrderCategory;
import com.infina.hissenet.entity.enums.OrderType;

public record OrderCreateRequest(
		Long customerId,
		Long stockId,
		OrderCategory category,
		OrderType type,
		BigDecimal quantity,
		BigDecimal price
) {

}
