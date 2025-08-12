package com.infina.hissenet.exception.customer;

import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.utils.MessageUtils;

public class CustomerNotFoundException extends NotFoundException {

    public CustomerNotFoundException(Long id) {
        super(MessageUtils.getMessage("customer.not.found.id", id));
    }

    public CustomerNotFoundException(String messageKey, Object... args) {
        super(MessageUtils.getMessage(messageKey, args));
    }
}