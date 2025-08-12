package com.infina.hissenet.exception.wallet;

import com.infina.hissenet.utils.MessageUtils;

public class WalletLimitExceededException extends RuntimeException {
    public WalletLimitExceededException(String limitType) {
        super(MessageUtils.getMessage("wallet.limit.exceeded." + limitType.toLowerCase().replace(" ", ".").replace("transaction.count", "transaction.count")));
    }
}