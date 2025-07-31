package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record StockPriceUpdateRequest(
        @NotNull
        BigDecimal currentPrice,

        BigDecimal openPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,

        @NotNull
        LocalDateTime timestamp
) {}