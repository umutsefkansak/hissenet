package com.infina.hissenet.exception;

public class AddressNotFoundException extends NotFoundException {
    public AddressNotFoundException(Long id) {
        super("Address not found with id: " + id);
    }
}