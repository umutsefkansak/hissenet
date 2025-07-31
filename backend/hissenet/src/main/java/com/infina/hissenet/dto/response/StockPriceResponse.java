package com.infina.hissenet.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record StockPriceResponse(
        Long id,
        Long stockId,
        BigDecimal currentPrice,
        BigDecimal openPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        LocalDateTime timestamp
) {}
