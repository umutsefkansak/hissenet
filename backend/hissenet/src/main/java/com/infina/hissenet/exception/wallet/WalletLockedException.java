package com.infina.hissenet.exception.wallet;

import com.infina.hissenet.utils.MessageUtils;

public class WalletLockedException extends RuntimeException {
    public WalletLockedException() {
        super(MessageUtils.getMessage("wallet.locked"));
    }
}
