package com.infina.hissenet.exception.stock;

import java.math.BigDecimal;

public class BadRequestStockException extends RuntimeException {

    private final Long customerId;
    private final String stockCode;
    private final BigDecimal requestedQuantity;
    private final BigDecimal totalOwned;
    private final BigDecimal blockedQuantity;
    private final BigDecimal availableQuantity;

    public BadRequestStockException(Long customerId, String stockCode,
                                    BigDecimal requestedQuantity, BigDecimal totalOwned,
                                    BigDecimal blockedQuantity, BigDecimal availableQuantity) {
        super(String.format(
                "Yetersiz hisse! Müşteri: %d, Hisse: %s, İstenen: %s, Toplam: %s, Blokede (T+2): %s, Satılabilir: %s. " +
                        "T+2 settlement nedeniyle satılan hisseler geçici olarak bloke edilmiştir.",
                customerId, stockCode, requestedQuantity, totalOwned, blockedQuantity, availableQuantity
        ));

        this.customerId = customerId;
        this.stockCode = stockCode;
        this.requestedQuantity = requestedQuantity;
        this.totalOwned = totalOwned;
        this.blockedQuantity = blockedQuantity;
        this.availableQuantity = availableQuantity;
    }

    // Getters
    public Long getCustomerId() { return customerId; }
    public String getStockCode() { return stockCode; }
    public BigDecimal getRequestedQuantity() { return requestedQuantity; }
    public BigDecimal getTotalOwned() { return totalOwned; }
    public BigDecimal getBlockedQuantity() { return blockedQuantity; }
    public BigDecimal getAvailableQuantity() { return availableQuantity; }
}
