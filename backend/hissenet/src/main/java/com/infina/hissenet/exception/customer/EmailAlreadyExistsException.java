package com.infina.hissenet.exception.customer;

import com.infina.hissenet.utils.MessageUtils;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super(MessageUtils.getMessage("customer.email.already.exists", email));
    }
}