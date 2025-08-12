package com.infina.hissenet.exception.auth;

import com.infina.hissenet.utils.MessageUtils;

public class LoginException extends RuntimeException {
    public LoginException() {
        super(MessageUtils.getMessage("auth.login.failed"));
    }
}
