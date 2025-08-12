package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.PortfolioType;
import com.infina.hissenet.entity.enums.RiskProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PortfolioCreateRequest(
        @NotBlank(message = "{validation.portfolio.name.required}")
        @Size(min = 2, max = 100, message = "{validation.portfolio.name.size}")
        String portfolioName,

        @Size(max = 500, message = "{validation.portfolio.description.size}")
        String description,

        @NotNull(message = "{validation.risk.profile.required}")
        RiskProfile riskProfile,

        @NotNull(message = "{validation.portfolio.type.required}")
        PortfolioType portfolioType
) {} 