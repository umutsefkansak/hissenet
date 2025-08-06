package com.infina.hissenet.dto.response;

import java.math.BigDecimal;

public record PortfolioStockResponse(
    String stockCode,
    String stockName,
    BigDecimal netQuantity,
    BigDecimal averagePrice,
    BigDecimal currentPrice,
    BigDecimal currentValue,
    BigDecimal totalCost,
    BigDecimal profitLoss,
    BigDecimal profitLossPercentage
) {} 