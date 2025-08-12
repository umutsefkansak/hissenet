package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateWalletRequest(
        @NotNull(message = "{validation.customer.id.required}")
        Long customerId,

        @NotNull(message = "{validation.balance.required}")
        @DecimalMin(value = "0.0", message = "{validation.balance.min}")
        BigDecimal balance,

        @NotNull(message = "{validation.currency.required}")
        @Size(min = 3, max = 3, message = "{validation.currency.size}")
        String currency,

        BigDecimal dailyLimit,
        BigDecimal monthlyLimit,
        BigDecimal maxTransactionAmount,
        BigDecimal minTransactionAmount,
        Integer maxDailyTransactionCount,
        BigDecimal defaultBlockedBalance,
        BigDecimal defaultAvailableBalance) {
}
