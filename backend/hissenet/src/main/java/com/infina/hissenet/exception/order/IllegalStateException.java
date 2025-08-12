package com.infina.hissenet.exception.order;

import com.infina.hissenet.utils.MessageUtils;

public class IllegalStateException extends RuntimeException {
    public IllegalStateException(){
        super(MessageUtils.getMessage("order.market.closed"));
    }
}