package com.infina.hissenet.exception.order;

import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.utils.MessageUtils;

public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException(Long id) {
        super(MessageUtils.getMessage("order.not.found", id));
    }
}
