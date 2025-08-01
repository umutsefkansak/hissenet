package com.infina.hissenet.exception.mail;


import com.infina.hissenet.exception.NotFoundException;

public class VerificationCodeNotFoundException extends NotFoundException {
    public VerificationCodeNotFoundException(String message) {
        super(message);
    }
}