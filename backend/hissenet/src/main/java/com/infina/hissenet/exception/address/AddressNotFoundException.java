package com.infina.hissenet.exception.address;

import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.utils.MessageUtils;

public class AddressNotFoundException extends NotFoundException {
    public AddressNotFoundException(Long id) {
        super(MessageUtils.getMessage("address.not.found", id));
    }

    public AddressNotFoundException(String messageKey, Object... args) {
        super(MessageUtils.getMessage(messageKey, args));
    }
}