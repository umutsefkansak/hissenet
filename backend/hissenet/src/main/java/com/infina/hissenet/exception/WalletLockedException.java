package com.infina.hissenet.exception;

public class WalletLockedException extends RuntimeException {
    public WalletLockedException() {
        super("Wallet is locked");
    }
}
