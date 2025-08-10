package com.infina.hissenet.constants;


import java.math.BigDecimal;

public final class CustomerConstants {

    private CustomerConstants() {

    }

    public static final String INDIVIDUAL_CUSTOMER_PREFIX = "IND";
    public static final String CORPORATE_CUSTOMER_PREFIX = "CORP";

    public static final String DEFAULT_NATIONALITY = "TR";
    public static final Boolean DEFAULT_KYC_VERIFIED = false;

    public static final int CUSTOMER_NUMBER_UUID_LENGTH = 12;

    public static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.00010");
}