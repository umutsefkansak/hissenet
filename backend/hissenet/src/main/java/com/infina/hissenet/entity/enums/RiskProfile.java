package com.infina.hissenet.entity.enums;

public enum RiskProfile {
    CONSERVATIVE("Muhafazakar"),
    MODERATE("Orta Risk"),
    AGGRESSIVE("Agresif"),
    VERY_AGGRESSIVE("Çok Agresif");

    private final String displayName;

    RiskProfile(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}