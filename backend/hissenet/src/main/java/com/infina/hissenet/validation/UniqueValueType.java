package com.infina.hissenet.validation;

public enum UniqueValueType {
    EMPLOYEE_EMAIL("This email address is already registered in the system"),
    EMAIL("This email address is already registered in the system"),
    TAX_NUMBER("This tax number is already registered in the system"),
    TC_NUMBER("This ID number is already registered in the system");




    private final String defaultMessage;

    UniqueValueType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
