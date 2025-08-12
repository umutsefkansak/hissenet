package com.infina.hissenet.entity.enums;

public enum CustomerType {
    INDIVIDUAL("Bireysel"),
    CORPORATE("Kurumsal");

    private final String displayName;

    CustomerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}