package com.infina.hissenet.exception;

public class TransactionAlreadyCompletedException extends RuntimeException {
    public TransactionAlreadyCompletedException(Long transactionId) {
        super("Transaction is already completed: " + transactionId);
    }
}