package com.infina.hissenet.exception.customer;

import com.infina.hissenet.exception.NotFoundException;

public class CustomerNotFoundException extends NotFoundException {
    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }
}