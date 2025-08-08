package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.Currency;
import com.infina.hissenet.entity.enums.Exchange;
import com.infina.hissenet.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StockCreateRequest(
        @NotBlank(message = "{validation.stock.ticker.required}")
        String ticker,

        @NotBlank(message = "{validation.stock.issuer.name.required}")
        String issuerName,

        @NotNull(message = "{validation.currency.required}")
        Currency currency,

        @NotNull(message = "{validation.stock.exchange.required}")
        Exchange exchange,

        Integer lotSize,

        @NotNull(message = "{validation.status.required}")
        Status status
) {}
