package com.infina.hissenet.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String  dType) {
        super(dType+" türündeki kullanıcı bulunamadi");
    }
}
