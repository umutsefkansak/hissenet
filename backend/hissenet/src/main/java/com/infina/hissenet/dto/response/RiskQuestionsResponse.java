package com.infina.hissenet.dto.response;

import com.infina.hissenet.config.RiskAssessmentConfig;

import java.util.List;

public record RiskQuestionsResponse(
        List<RiskAssessmentConfig.RiskQuestion> questions
) {}