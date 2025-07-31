package com.infina.hissenet.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockHistoryUpdateRequest(
        LocalDateTime recordDate,
        LocalDate dataDate,
        BigDecimal openPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal closePrice
) {

}
