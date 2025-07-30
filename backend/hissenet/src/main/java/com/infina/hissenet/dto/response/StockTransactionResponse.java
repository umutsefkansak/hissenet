package com.infina.hissenet.dto.response;

import com.infina.hissenet.entity.enums.MarketOrderType;
import com.infina.hissenet.entity.enums.StockTransactionType;
import com.infina.hissenet.entity.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockTransactionResponse(
    Long id,
    Long portfolioId,
    String portfolioName,
    Long stockId,
    String stockTicker,
    String stockName,
    Long orderId,
    StockTransactionType transactionType,
    TransactionStatus transactionStatus,
    Integer quantity,
    BigDecimal price,
    BigDecimal totalAmount,
    BigDecimal commission,
    BigDecimal tax,
    BigDecimal otherFees,
    MarketOrderType marketOrderType,
    BigDecimal limitPrice,
    BigDecimal executionPrice,
    LocalDateTime transactionDate,
    LocalDateTime settlementDate,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 