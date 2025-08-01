package com.infina.hissenet.exception.wallet;

public class WalletAlreadyExistsException extends RuntimeException {
    public WalletAlreadyExistsException(Long customerId) {
        super("This customer already has wallet: " + customerId);
    }
}