package com.infina.hissenet.dto.response;

import com.infina.hissenet.entity.enums.Currency;
import com.infina.hissenet.entity.enums.Exchange;
import com.infina.hissenet.entity.enums.Status;

public record StockResponse(
        Long id,
        String ticker,
        String issuerName,
        Currency currency,
        Exchange exchange,
        Integer lotSize,
        Status status
) {}