package com.infina.hissenet.exception.common;

import com.infina.hissenet.utils.MessageUtils;

public class RateLimitException extends RuntimeException {
    public RateLimitException() {
        super(MessageUtils.getMessage("rate.limit.exceeded"));
    }

    public RateLimitException(String messageKey, Object... args) {
        super(MessageUtils.getMessage(messageKey, args));
    }
}