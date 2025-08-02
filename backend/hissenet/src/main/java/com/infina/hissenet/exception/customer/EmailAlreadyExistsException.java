package com.infina.hissenet.exception.customer;

public class EmailAlreadyExistsException  extends RuntimeException {
    public EmailAlreadyExistsException(String name) {
        super("Email already exists: " + name);
    }
}