package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockHistoryCreateRequest(
        @NotNull
        Long stockId,

        @NotNull
        LocalDateTime recordDate,

        @NotNull
        LocalDate dataDate,

        @NotNull
        BigDecimal openPrice,

        @NotNull
        BigDecimal highPrice,

        @NotNull
        BigDecimal lowPrice,

        @NotNull
        BigDecimal closePrice
) {}
