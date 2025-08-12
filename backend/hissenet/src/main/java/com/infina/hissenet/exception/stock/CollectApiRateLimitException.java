package com.infina.hissenet.exception.stock;

public class CollectApiRateLimitException extends RuntimeException {
    public CollectApiRateLimitException(String message) {
        super(message);
    }
}
