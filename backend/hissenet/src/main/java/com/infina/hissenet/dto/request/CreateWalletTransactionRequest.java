package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateWalletTransactionRequest(

        @NotNull(message = "{validation.wallet.id.required}")
        Long walletId,

        @NotNull(message = "{validation.amount.required}")
        @DecimalMin(value = "0.01", message = "{validation.amount.min}")
        BigDecimal amount,

        @NotNull(message = "{validation.transaction.type.required}")
        TransactionType transactionType,



        @DecimalMin(value = "0.0", message = "{validation.fee.amount.min}")
        BigDecimal feeAmount,

        String source,
        String destination


) {
}
