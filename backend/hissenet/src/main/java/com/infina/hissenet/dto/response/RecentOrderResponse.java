package com.infina.hissenet.dto.response;

import java.math.BigDecimal;

import com.infina.hissenet.entity.enums.OrderType;

public record RecentOrderResponse(
		String stockCode,
		BigDecimal totalAmount,
		OrderType type
) {

}
