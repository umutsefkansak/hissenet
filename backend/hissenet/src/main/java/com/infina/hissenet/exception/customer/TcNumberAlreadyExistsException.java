package com.infina.hissenet.exception.customer;

import com.infina.hissenet.utils.MessageUtils;

public class TcNumberAlreadyExistsException extends RuntimeException {
    public TcNumberAlreadyExistsException(String tcNumber) {
        super(MessageUtils.getMessage("customer.tc.number.already.exists", tcNumber));
    }
}