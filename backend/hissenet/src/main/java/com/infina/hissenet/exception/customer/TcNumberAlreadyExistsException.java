package com.infina.hissenet.exception.customer;

public class TcNumberAlreadyExistsException extends RuntimeException {
    public TcNumberAlreadyExistsException(String name) {
        super("Tc number already exists: " + name);
    }
}