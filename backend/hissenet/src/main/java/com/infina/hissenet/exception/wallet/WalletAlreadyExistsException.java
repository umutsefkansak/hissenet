package com.infina.hissenet.exception.wallet;

import com.infina.hissenet.utils.MessageUtils;

public class WalletAlreadyExistsException extends RuntimeException {
    public WalletAlreadyExistsException(Long customerId) {
        super(MessageUtils.getMessage("wallet.already.exists", customerId));
    }
}