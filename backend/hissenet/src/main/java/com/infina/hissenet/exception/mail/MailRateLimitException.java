package com.infina.hissenet.exception.mail;


import com.infina.hissenet.exception.common.RateLimitException;

public class MailRateLimitException extends RateLimitException {
    public MailRateLimitException(String message) {
        super(message);
    }
}