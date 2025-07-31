package com.infina.hissenet.exception;

public class WalletNotFoundException extends NotFoundException{
    public WalletNotFoundException(Long customerId) {
        super("Wallet not found for customer ID: " + customerId);
    }
}
