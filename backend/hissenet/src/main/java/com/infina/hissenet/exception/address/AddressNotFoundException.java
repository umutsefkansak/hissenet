package com.infina.hissenet.exception.address;

import com.infina.hissenet.exception.common.NotFoundException;

public class AddressNotFoundException extends NotFoundException {
    public AddressNotFoundException(Long id) {
        super("Address not found with id: " + id);
    }
}