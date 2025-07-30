package com.infina.hissenet.constants;


public final class CustomerConstants {

    private CustomerConstants() {

    }

    // Customer Number Prefixes
    public static final String INDIVIDUAL_CUSTOMER_PREFIX = "IND";
    public static final String CORPORATE_CUSTOMER_PREFIX = "CORP";

    // Default Values
    public static final String DEFAULT_NATIONALITY = "TR";
    public static final Boolean DEFAULT_KYC_VERIFIED = false;

    // Customer Number Generation
    public static final int CUSTOMER_NUMBER_UUID_LENGTH = 12;
}