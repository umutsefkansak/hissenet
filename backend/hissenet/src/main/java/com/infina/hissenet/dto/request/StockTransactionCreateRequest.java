package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.MarketOrderType;
import com.infina.hissenet.entity.enums.StockTransactionType;
import com.infina.hissenet.entity.enums.TransactionStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockTransactionCreateRequest(
    @NotNull(message = "{validation.portfolio.id.required}")
    Long portfolioId,

    Long orderId,

    @NotNull(message = "{validation.stock.transaction.type.required}")
    StockTransactionType transactionType,

    @NotNull(message = "{validation.stock.transaction.status.required}")
    TransactionStatus transactionStatus,

    @NotNull(message = "{validation.quantity.required}")
    @Positive(message = "{validation.quantity.positive}")
    Integer quantity,

    @NotNull(message = "{validation.price.required}")
    @DecimalMin(value = "0.0", message = "{validation.price.negative}")
    BigDecimal price,

    @NotNull(message = "{validation.total.amount.required}")
    @DecimalMin(value = "0.0", message = "{validation.total.amount.negative}")
    BigDecimal totalAmount,

    @DecimalMin(value = "0.0", message = "{validation.commission.negative}")
    BigDecimal commission,

    @DecimalMin(value = "0.0", message = "{validation.tax.negative}")
    BigDecimal tax,

    @DecimalMin(value = "0.0", message = "{validation.other.fees.negative}")
    BigDecimal otherFees,

    MarketOrderType marketOrderType,

    @DecimalMin(value = "0.0", message = "{validation.limit.price.negative}")
    BigDecimal limitPrice,

    @DecimalMin(value = "0.0", message = "{validation.execution.price.negative}")
    BigDecimal executionPrice,

    LocalDateTime transactionDate,

    LocalDateTime settlementDate,

    String notes
) {
    public StockTransactionCreateRequest {
        // Default değerler
        if (commission == null) commission = BigDecimal.ZERO;
        if (tax == null) tax = BigDecimal.ZERO;
        if (otherFees == null) otherFees = BigDecimal.ZERO;
    }
} 