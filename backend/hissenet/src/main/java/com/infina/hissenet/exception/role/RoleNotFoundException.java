package com.infina.hissenet.exception.role;

import com.infina.hissenet.exception.common.NotFoundException;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(Long id) {
        super("Role not found with id: " + id);
    }

    public RoleNotFoundException(String name) {
        super("Role not found with name: " + name);
    }
}