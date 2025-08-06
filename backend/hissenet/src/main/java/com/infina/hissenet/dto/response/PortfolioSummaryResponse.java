package com.infina.hissenet.dto.response;

import com.infina.hissenet.entity.enums.PortfolioType;
import com.infina.hissenet.entity.enums.RiskProfile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PortfolioSummaryResponse(
    Long id,
    String portfolioName,
    BigDecimal totalValue,
    BigDecimal totalProfitLoss,
    BigDecimal profitLossPercentage,
    RiskProfile riskProfile,
    PortfolioType portfolioType,
    Boolean isActive,
    LocalDateTime lastRebalanceDate,
    List<StockTransactionResponse> list
) {} 