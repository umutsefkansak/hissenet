package com.infina.hissenet.exception;

public class TransactionNotFoundException extends NotFoundException {
    public TransactionNotFoundException(Long transactionId) {
        super("Transaction not found with ID: " + transactionId);
    }
}