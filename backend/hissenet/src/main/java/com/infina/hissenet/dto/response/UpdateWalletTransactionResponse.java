package com.infina.hissenet.dto.response;

import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateWalletTransactionResponse(
        Long id,
        Long walletId,
        BigDecimal amount,
        TransactionType transactionType,
        String description,
        TransactionStatus transactionStatus,
        LocalDateTime transactionDate,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        String referenceNumber,
        BigDecimal feeAmount,
        BigDecimal taxAmount,
        String source,
        String destination
) {
}
