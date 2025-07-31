package com.infina.hissenet.exception;

public class WalletNotActiveException extends RuntimeException {
    public WalletNotActiveException() {
        super("Wallet is not active");
    }
}