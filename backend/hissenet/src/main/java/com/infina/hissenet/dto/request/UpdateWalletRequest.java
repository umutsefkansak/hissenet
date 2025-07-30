package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.Status;

import java.math.BigDecimal;

public record UpdateWalletRequest(
        BigDecimal dailyLimit,
        BigDecimal monthlyLimit,
        BigDecimal maxTransactionAmount,
        BigDecimal minTransactionAmount,
        Integer maxDailyTransactionCount,
        Boolean isLocked,
        Status walletStatus
) {
}
