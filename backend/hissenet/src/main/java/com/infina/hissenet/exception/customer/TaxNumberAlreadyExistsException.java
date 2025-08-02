package com.infina.hissenet.exception.customer;

public class TaxNumberAlreadyExistsException extends RuntimeException {
    public TaxNumberAlreadyExistsException(String name) {
        super("Tax number already exists: " + name);
    }
}