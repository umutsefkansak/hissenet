package com.infina.hissenet.entity.enums;

public enum RiskProfile {
    CONSERVATIVE("Düşük Risk"),
    MODERATE("Orta Risk"),
    AGGRESSIVE("Yüksek Risk"),
    VERY_AGGRESSIVE("Çok Yüksek Risk");

    private final String displayName;

    RiskProfile(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}