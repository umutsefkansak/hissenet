package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateWalletTransactionRequest(

        @NotNull(message = "Wallet ID cannot be null")
        Long walletId,

        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Transaction type cannot be null")
        TransactionType transactionType,



        @DecimalMin(value = "0.0", message = "Fee amount cannot be negative")
        BigDecimal feeAmount,

        String source,
        String destination


) {
}
