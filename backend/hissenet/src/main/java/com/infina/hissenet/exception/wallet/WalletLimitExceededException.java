package com.infina.hissenet.exception.wallet;


public class WalletLimitExceededException extends RuntimeException {
    public WalletLimitExceededException(String limitType) {
        super(limitType + " limit exceeded");
    }
}