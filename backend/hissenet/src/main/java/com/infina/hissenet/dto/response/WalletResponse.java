package com.infina.hissenet.dto.response;

import com.infina.hissenet.entity.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record


WalletResponse(
        Long id,
        Long customerId,
        BigDecimal balance,
        String currency,
        BigDecimal dailyLimit,
        BigDecimal monthlyLimit,
        BigDecimal dailyUsedAmount,
        BigDecimal monthlyUsedAmount,
        BigDecimal maxTransactionAmount,
        BigDecimal minTransactionAmount,
        Integer dailyTransactionCount,
        Integer maxDailyTransactionCount,
        LocalDateTime lastTransactionDate,
        LocalDate lastResetDate,
        Boolean isLocked,
        Status walletStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
