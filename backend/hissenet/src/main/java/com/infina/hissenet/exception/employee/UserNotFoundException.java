package com.infina.hissenet.exception.employee;

import com.infina.hissenet.exception.common.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String  dType) {
        super(dType+" türündeki kullanıcı bulunamadi");
    }
}
