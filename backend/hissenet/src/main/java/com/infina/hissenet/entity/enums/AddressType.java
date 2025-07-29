package com.infina.hissenet.entity.enums;

public enum AddressType {
    HOME("Ev Adresi"),
    WORK("İş Adresi"),
    BILLING("Fatura Adresi"),
    CORRESPONDENCE("Yazışma Adresi"),
    OTHER("Diğer");

    private final String displayName;

    AddressType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
