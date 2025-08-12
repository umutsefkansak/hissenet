package com.infina.hissenet.exception.customer;

import com.infina.hissenet.utils.MessageUtils;

public class TaxNumberAlreadyExistsException extends RuntimeException {
    public TaxNumberAlreadyExistsException(String taxNumber) {
        super(MessageUtils.getMessage("customer.tax.number.already.exists", taxNumber));
    }
}