package com.infina.hissenet.exception;

public class StockNotFoundException extends NotFoundException {
	public StockNotFoundException(Long id) {
		super("Stock not found with id: " + id);
	}

}
