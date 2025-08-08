package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockHistoryCreateRequest(
        @NotNull(message = "{validation.stock.id.required}")
        Long stockId,

        @NotNull(message = "{validation.stock.history.record.date.required}")
        LocalDateTime recordDate,

        @NotNull(message = "{validation.stock.history.data.date.required}")
        LocalDate dataDate,

        @NotNull(message = "{validation.stock.history.open.price.required}")
        BigDecimal openPrice,

        @NotNull(message = "{validation.stock.history.high.price.required}")
        BigDecimal highPrice,

        @NotNull(message = "{validation.stock.history.low.price.required}")
        BigDecimal lowPrice,

        @NotNull(message = "{validation.stock.history.close.price.required}")
        BigDecimal closePrice
) {}
