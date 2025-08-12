package com.infina.hissenet.constants;


import java.math.BigDecimal;

public final class WalletConstants {

    private WalletConstants() {

    }

    // Default Values
    public static final BigDecimal DEFAULT_BALANCE = BigDecimal.ZERO;
    public static final String DEFAULT_CURRENCY = "TRY";
    public static final BigDecimal DEFAULT_BLOCKED_BALANCE = BigDecimal.ZERO;
    public static final BigDecimal DEFAULT_AVAILABLE_BALANCE = BigDecimal.ZERO;
    // Limits
    public static final BigDecimal DEFAULT_DAILY_LIMIT = BigDecimal.valueOf(90_000_000);
    public static final BigDecimal DEFAULT_MONTHLY_LIMIT = BigDecimal.valueOf(900_000_000);
    public static final BigDecimal DEFAULT_MAX_TRANSACTION_AMOUNT = BigDecimal.valueOf(50_000_000);
    public static final BigDecimal DEFAULT_MIN_TRANSACTION_AMOUNT = BigDecimal.valueOf(10);

    // Transaction Counts
    public static final Integer DEFAULT_MAX_DAILY_TRANSACTION_COUNT = 500;
    public static final Integer DEFAULT_DAILY_TRANSACTION_COUNT = 0;
    // Status
    public static final Boolean DEFAULT_IS_LOCKED = false;
}
