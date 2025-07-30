package com.infina.hissenet.constants;


import com.infina.hissenet.entity.enums.PortfolioType;
import com.infina.hissenet.entity.enums.RiskProfile;

import java.math.BigDecimal;

public final class PortfolioConstants {

    private PortfolioConstants() {

    }

    // Default Names
    public static final String DEFAULT_PORTFOLIO_NAME = "Ana Portföy";
    public static final String DEFAULT_PORTFOLIO_DESCRIPTION = "Otomatik oluşturulan ana portföy";

    // Default Values
    public static final BigDecimal DEFAULT_TOTAL_VALUE = BigDecimal.ZERO;
    public static final BigDecimal DEFAULT_TOTAL_COST = BigDecimal.ZERO;
    public static final BigDecimal DEFAULT_TOTAL_PROFIT_LOSS = BigDecimal.ZERO;
    public static final BigDecimal DEFAULT_PROFIT_LOSS_PERCENTAGE = BigDecimal.ZERO;

    // Default Enums
    public static final RiskProfile DEFAULT_RISK_PROFILE = RiskProfile.MODERATE;
    public static final PortfolioType DEFAULT_PORTFOLIO_TYPE = PortfolioType.BALANCED;

    // Default Status
    public static final Boolean DEFAULT_IS_ACTIVE = true;
}