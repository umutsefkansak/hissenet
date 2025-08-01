package com.infina.hissenet.exception.transaction;

import com.infina.hissenet.exception.common.NotFoundException;

public class TransactionNotFoundException extends NotFoundException {
    public TransactionNotFoundException(Long transactionId) {
        super("Transaction not found with ID: " + transactionId);
    }
}