package com.infina.hissenet.validation;

public enum UniqueValueType {
    EMPLOYEE_EMAIL("customer.email.already.exists"),
    CUSTOMER_EMAIL("customer.email.already.exists"),
    TAX_NUMBER("customer.tax.number.already.exists"),
    TC_NUMBER("customer.tc.number.already.exists");

    private final String messageKey;

    UniqueValueType(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}