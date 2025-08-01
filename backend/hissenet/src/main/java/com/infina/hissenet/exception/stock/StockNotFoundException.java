package com.infina.hissenet.exception.stock;

import com.infina.hissenet.exception.common.NotFoundException;

public class StockNotFoundException extends NotFoundException {
	public StockNotFoundException(Long id) {
		super("Stock not found with id: " + id);
	}

}
