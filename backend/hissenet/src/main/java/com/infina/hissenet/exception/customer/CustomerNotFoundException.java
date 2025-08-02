package com.infina.hissenet.exception.customer;

import com.infina.hissenet.exception.common.NotFoundException;
public class CustomerNotFoundException extends NotFoundException {
    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }


    public CustomerNotFoundException(String message) {
        super(message);
    }
}