package com.infina.hissenet.exception.order;

import com.infina.hissenet.exception.common.NotFoundException;

public class OrderNotFoundException extends NotFoundException {
	public OrderNotFoundException(Long id) {
        super("Order not found with id: " + id);
    }
}
