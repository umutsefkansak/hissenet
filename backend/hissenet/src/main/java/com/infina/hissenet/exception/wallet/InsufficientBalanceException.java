package com.infina.hissenet.exception.wallet;


import java.math.BigDecimal;
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(BigDecimal required, BigDecimal available) {
        super("Insufficient balance. Required: " + required + ", Available: " + available);
    }
}