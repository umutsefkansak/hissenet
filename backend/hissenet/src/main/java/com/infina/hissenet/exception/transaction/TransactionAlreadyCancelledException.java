package com.infina.hissenet.exception.transaction;

public class TransactionAlreadyCancelledException extends RuntimeException {
    public TransactionAlreadyCancelledException(Long transactionId) {
        super("Transaction is already cancelled: " + transactionId);
    }
}