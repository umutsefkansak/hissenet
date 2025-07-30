package com.infina.hissenet.exception;

public class OrderNotFoundException extends NotFoundException{
	public OrderNotFoundException(Long id) {
        super("Order not found with id: " + id);
    }
}
