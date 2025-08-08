package com.infina.hissenet.exception.account;

import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.utils.MessageUtils;

public class AccountNotFoundException extends NotFoundException {
    public AccountNotFoundException(Long id) {
        super(MessageUtils.getMessage("account.not.found", id));
    }
}