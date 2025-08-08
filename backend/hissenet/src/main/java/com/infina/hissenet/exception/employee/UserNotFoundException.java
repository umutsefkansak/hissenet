package com.infina.hissenet.exception.employee;

import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.utils.MessageUtils;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String dType) {
        super(MessageUtils.getMessage("user.not.found", dType));
    }
}