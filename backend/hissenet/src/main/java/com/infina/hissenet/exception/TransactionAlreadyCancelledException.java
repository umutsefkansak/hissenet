package com.infina.hissenet.exception;

public class TransactionAlreadyCancelledException extends RuntimeException {
    public TransactionAlreadyCancelledException(Long transactionId) {
        super("Transaction is already cancelled: " + transactionId);
    }
}