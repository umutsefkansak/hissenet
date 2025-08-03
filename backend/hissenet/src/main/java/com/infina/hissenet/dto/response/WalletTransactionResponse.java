package com.infina.hissenet.dto.response;

import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletTransactionResponse(
        Long id,
        Long walletId,
        BigDecimal amount,
        TransactionType transactionType,
        TransactionStatus transactionStatus,
        LocalDateTime transactionDate,
        BigDecimal feeAmount,
        String source,
        String destination
) {
}
