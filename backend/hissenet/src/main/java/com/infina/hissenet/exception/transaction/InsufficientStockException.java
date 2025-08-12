package com.infina.hissenet.exception.transaction;

import com.infina.hissenet.utils.MessageUtils;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String stockCode) {
        super(MessageUtils.getMessage("stock.insufficient.quantity", stockCode));
    }
}
