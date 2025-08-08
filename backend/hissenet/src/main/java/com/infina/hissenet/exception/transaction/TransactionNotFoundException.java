package com.infina.hissenet.exception.transaction;

import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.utils.MessageUtils;

public class TransactionNotFoundException extends NotFoundException {
    public TransactionNotFoundException(Long transactionId) {
        super(MessageUtils.getMessage("transaction.not.found", transactionId));
    }
}