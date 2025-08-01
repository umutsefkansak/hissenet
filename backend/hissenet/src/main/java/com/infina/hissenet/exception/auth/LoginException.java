package com.infina.hissenet.exception.auth;

public class LoginException extends RuntimeException {
    public LoginException() {
        super("Username or password is incorrect");
    }
}
