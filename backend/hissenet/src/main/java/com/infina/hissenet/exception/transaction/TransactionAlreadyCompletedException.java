package com.infina.hissenet.exception.transaction;

public class TransactionAlreadyCompletedException extends RuntimeException {
    public TransactionAlreadyCompletedException(Long transactionId) {
        super("Transaction is already completed: " + transactionId);
    }
}