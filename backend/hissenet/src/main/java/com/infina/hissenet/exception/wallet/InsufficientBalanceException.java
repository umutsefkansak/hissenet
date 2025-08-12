package com.infina.hissenet.exception.wallet;

import com.infina.hissenet.utils.MessageUtils;
import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(BigDecimal required, BigDecimal available) {
        super(MessageUtils.getMessage("wallet.insufficient.balance", required, available));
    }
}