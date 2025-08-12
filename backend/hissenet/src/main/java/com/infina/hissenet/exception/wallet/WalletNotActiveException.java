package com.infina.hissenet.exception.wallet;

import com.infina.hissenet.utils.MessageUtils;

public class WalletNotActiveException extends RuntimeException {
    public WalletNotActiveException() {
        super(MessageUtils.getMessage("wallet.not.active"));
    }
}