package com.infina.hissenet.entity.enums;

public enum IncomeRange {
    RANGE_0_10K("0 - 10.000 TL"),
    RANGE_10K_25K("10.001 - 25.000 TL"),
    RANGE_25K_50K("25.001 - 50.000 TL"),
    RANGE_50K_100K("50.001 - 100.000 TL"),
    RANGE_ABOVE_100K("100.000 TL üzeri");

    private final String displayName;

    IncomeRange(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}