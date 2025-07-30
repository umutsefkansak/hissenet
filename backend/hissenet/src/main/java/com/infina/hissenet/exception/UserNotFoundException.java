package com.infina.hissenet.exception;

public class UserNotFoundException extends NotFoundException{
    public UserNotFoundException(String  dType) {
        super(dType+" türündeki kullanıcı bulunamadi");
    }
}
