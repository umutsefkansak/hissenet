package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.Currency;
import com.infina.hissenet.entity.enums.Exchange;
import com.infina.hissenet.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StockCreateRequest(
        @NotBlank
        String ticker,

        @NotBlank
        String issuerName,

        @NotNull
        Currency currency,

        @NotNull
        Exchange exchange,

        Integer lotSize,

        @NotNull
        Status status
) {}
