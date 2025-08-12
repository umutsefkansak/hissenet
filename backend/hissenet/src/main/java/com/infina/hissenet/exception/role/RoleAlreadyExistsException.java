package com.infina.hissenet.exception.role;

import com.infina.hissenet.utils.MessageUtils;

public class RoleAlreadyExistsException extends RuntimeException {
    public RoleAlreadyExistsException(String name) {
        super(MessageUtils.getMessage("role.already.exists", name));
    }
}