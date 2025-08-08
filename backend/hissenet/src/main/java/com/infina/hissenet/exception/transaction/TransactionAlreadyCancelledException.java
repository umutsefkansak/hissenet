package com.infina.hissenet.exception.transaction;

import com.infina.hissenet.utils.MessageUtils;

public class TransactionAlreadyCancelledException extends RuntimeException {
    public TransactionAlreadyCancelledException(Long transactionId) {
        super(MessageUtils.getMessage("transaction.already.cancelled", transactionId));
    }
}