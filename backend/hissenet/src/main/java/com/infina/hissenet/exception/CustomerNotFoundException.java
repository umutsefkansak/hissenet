package com.infina.hissenet.exception;

public class CustomerNotFoundException extends NotFoundException {
    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }
}