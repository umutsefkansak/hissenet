package com.infina.hissenet.exception.transaction;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String s) {
        super(s);
    }
}
