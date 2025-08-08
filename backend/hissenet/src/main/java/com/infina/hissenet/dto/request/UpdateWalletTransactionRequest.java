package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateWalletTransactionRequest(
        @NotNull(message = "{validation.transaction.status.required}")
        TransactionStatus transactionStatus
) {
}
