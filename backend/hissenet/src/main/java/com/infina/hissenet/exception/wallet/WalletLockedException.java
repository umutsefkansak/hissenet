package com.infina.hissenet.exception.wallet;

public class WalletLockedException extends RuntimeException {
    public WalletLockedException() {
        super("Wallet is locked");
    }
}
