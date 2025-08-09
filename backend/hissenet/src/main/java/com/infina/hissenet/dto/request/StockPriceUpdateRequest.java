package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockPriceUpdateRequest(
        @NotNull(message = "{validation.stock.price.current.required}")
        BigDecimal currentPrice,

        BigDecimal openPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,

        @NotNull(message = "{validation.stock.price.timestamp.required}")
        LocalDateTime timestamp
) {}