package com.infina.hissenet.exception.wallet;

import com.infina.hissenet.exception.common.NotFoundException;

public class WalletNotFoundException extends NotFoundException {
    public WalletNotFoundException(Long customerId) {
        super("Wallet not found for customer ID: " + customerId);
    }
}
