package com.infina.hissenet.exception.transaction;

import com.infina.hissenet.utils.MessageUtils;

public class TransactionAlreadyCompletedException extends RuntimeException {
    public TransactionAlreadyCompletedException(Long transactionId) {
        super(MessageUtils.getMessage("transaction.already.completed", transactionId));
    }
}