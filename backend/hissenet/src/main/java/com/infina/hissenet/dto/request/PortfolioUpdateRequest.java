package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.PortfolioType;
import com.infina.hissenet.entity.enums.RiskProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PortfolioUpdateRequest(
    @NotBlank(message = "Portföy adı boş olamaz")
    @Size(min = 2, max = 100, message = "Portföy adı 2-100 karakter arasında olmalıdır")
    String portfolioName,
    
    @Size(max = 500, message = "Açıklama 500 karakterden uzun olamaz")
    String description,
    
    @NotNull(message = "Risk profili boş olamaz")
    RiskProfile riskProfile,
    
    @NotNull(message = "Portföy türü boş olamaz")
    PortfolioType portfolioType,
    
    @NotNull(message = "Aktiflik durumu boş olamaz")
    Boolean isActive
) {} 