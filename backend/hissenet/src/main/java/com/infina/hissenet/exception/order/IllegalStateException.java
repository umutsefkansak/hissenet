package com.infina.hissenet.exception.order;

public class IllegalStateException extends RuntimeException {
    public IllegalStateException(){
        super("The market is closed.");
    }
}
