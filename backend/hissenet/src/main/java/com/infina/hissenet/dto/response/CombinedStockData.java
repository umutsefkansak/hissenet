package com.infina.hissenet.dto.response;

import java.math.BigDecimal;

public record CombinedStockData(
        String code,

        // Infina API
        BigDecimal closePrice,
        BigDecimal openPrice,
        BigDecimal changePrice,

        // Collect API
        BigDecimal rate,
        BigDecimal lastPrice,
        BigDecimal hacim,
        String hacimStr,
        BigDecimal min,
        BigDecimal max,
        String time,
        String text,
        String icon
) {}
