package com.infina.hissenet.exception.role;

import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.utils.MessageUtils;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(Long id) {
        super(MessageUtils.getMessage("role.not.found.id", id));
    }

    public RoleNotFoundException(String name) {
        super(MessageUtils.getMessage("role.not.found.name", name));
    }
}