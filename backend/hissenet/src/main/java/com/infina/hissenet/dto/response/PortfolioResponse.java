package com.infina.hissenet.dto.response;

import com.infina.hissenet.entity.enums.PortfolioType;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.entity.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PortfolioResponse(
    Long id,
    Long customerId,
    String customerName,
    String portfolioName,
    String description,
    BigDecimal totalValue,
    BigDecimal totalCost,
    BigDecimal totalProfitLoss,
    BigDecimal profitLossPercentage,
    RiskProfile riskProfile,
    PortfolioType portfolioType,
    Status status,
    Boolean isActive,
    LocalDateTime lastRebalanceDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 