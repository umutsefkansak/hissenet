package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateWalletRequest(
        @NotNull(message = "Customer ID cannot be empty")
        Long customerId,

        @NotNull(message = "Balance cannot be empty")
        @DecimalMin(value = "0.0", message = "Balance cannot be negative")
        BigDecimal balance,

        @NotNull(message = "Currency cannot be empty")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters")
        String currency,

        BigDecimal dailyLimit,
        BigDecimal monthlyLimit,
        BigDecimal maxTransactionAmount,
        BigDecimal minTransactionAmount,
        Integer maxDailyTransactionCount,
        BigDecimal defaultBlockedBalance,
        BigDecimal defaultAvailableBalance) {
}
