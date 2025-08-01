package com.infina.hissenet.exception.wallet;

public class WalletNotActiveException extends RuntimeException {
    public WalletNotActiveException() {
        super("Wallet is not active");
    }
}