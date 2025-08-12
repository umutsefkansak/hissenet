package com.infina.hissenet.exception.stock;

public class InfinaApiRateLimitException extends RuntimeException {
    public InfinaApiRateLimitException(String message) {
        super(message);
    }
}
