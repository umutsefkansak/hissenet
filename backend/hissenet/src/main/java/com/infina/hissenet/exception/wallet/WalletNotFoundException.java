package com.infina.hissenet.exception.wallet;

import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.utils.MessageUtils;

public class WalletNotFoundException extends NotFoundException {
    public WalletNotFoundException(Long customerId) {
        super(MessageUtils.getMessage("wallet.not.found", customerId));
    }
}
