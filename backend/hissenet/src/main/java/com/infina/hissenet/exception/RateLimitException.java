package com.infina.hissenet.exception;

public class RateLimitException extends RuntimeException {
    public RateLimitException() {
        super("Rate limit exceeded");
    }
}
