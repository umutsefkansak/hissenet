package com.infina.hissenet.entity.enums;

public enum AccountStatus {
    PENDING_VERIFICATION("Doğrulama Bekliyor"),
    ACTIVE("Aktif"),
    SUSPENDED("Askıya Alınmış"),
    BLOCKED("Bloke"),
    CLOSED("Kapatılmış");

    private final String displayName;

    AccountStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}