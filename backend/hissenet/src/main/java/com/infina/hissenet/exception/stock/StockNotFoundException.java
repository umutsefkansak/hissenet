package com.infina.hissenet.exception.stock;

import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.utils.MessageUtils;

public class StockNotFoundException extends NotFoundException {
	public StockNotFoundException(Long id) {
		super(MessageUtils.getMessage("stock.not.found", id));
	}

	public StockNotFoundException(String code) {
		super(MessageUtils.getMessage("stock.not.found", code));
	}
}