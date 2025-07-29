package com.infina.hissenet.entity.enums;

public enum EmployeeStatus {
    ACTIVE("Aktif"),
    INACTIVE("Pasif"),
    TERMINATED("İşten Çıkarıldı"),
    ON_LEAVE("İzinli"),
    SUSPENDED("Askıya Alındı");

    private final String description;

    EmployeeStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}