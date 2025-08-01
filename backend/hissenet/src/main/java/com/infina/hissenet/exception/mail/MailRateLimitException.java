package com.infina.hissenet.exception.mail;


import com.infina.hissenet.exception.RateLimitException;

public class MailRateLimitException extends RateLimitException {
    public MailRateLimitException(String message) {
        super(message);
    }
}