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
    @NotNull(message = "Portfolio ID boş olamaz")
    Long portfolioId,

    @NotNull(message = "Stock ID boş olamaz")
    Long stockId,

    Long orderId,

    @NotNull(message = "İşlem türü boş olamaz")
    StockTransactionType transactionType,

    @NotNull(message = "İşlem durumu boş olamaz")
    TransactionStatus transactionStatus,

    @NotNull(message = "Miktar boş olamaz")
    @Positive(message = "Miktar pozitif olmalıdır")
    Integer quantity,

    @NotNull(message = "Fiyat boş olamaz")
    @DecimalMin(value = "0.0", message = "Fiyat negatif olamaz")
    BigDecimal price,

    @NotNull(message = "Toplam tutar boş olamaz")
    @DecimalMin(value = "0.0", message = "Toplam tutar negatif olamaz")
    BigDecimal totalAmount,

    @DecimalMin(value = "0.0", message = "Komisyon negatif olamaz")
    BigDecimal commission,

    @DecimalMin(value = "0.0", message = "Vergi negatif olamaz")
    BigDecimal tax,

    @DecimalMin(value = "0.0", message = "Diğer ücretler negatif olamaz")
    BigDecimal otherFees,

    MarketOrderType marketOrderType,

    @DecimalMin(value = "0.0", message = "Limit fiyatı negatif olamaz")
    BigDecimal limitPrice,

    @DecimalMin(value = "0.0", message = "Gerçekleşme fiyatı negatif olamaz")
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