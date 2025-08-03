package com.infina.hissenet.dto.response;

import com.infina.hissenet.entity.enums.RiskProfile;

public record RiskAssessmentCalculateResponse(
        Integer totalScore,
        RiskProfile riskProfile,
        String riskDescription
) {}