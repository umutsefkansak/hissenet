package com.infina.hissenet.dto.response;

import java.math.BigDecimal;

public record PortfolioStockQuantityResponse(
		String stockCode,
		BigDecimal netQuantity,
		BigDecimal averagePrice
) {

}
