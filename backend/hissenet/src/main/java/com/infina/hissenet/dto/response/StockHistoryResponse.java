package com.infina.hissenet.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockHistoryResponse(
        Long id,
        Long stockId,
        LocalDateTime recordDate,
        LocalDate dataDate,
        BigDecimal openPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal closePrice
) {}
