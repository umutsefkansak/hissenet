package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.RiskAssessmentCalculateRequest;
import com.infina.hissenet.dto.response.RiskAssessmentCalculateResponse;
import com.infina.hissenet.dto.response.RiskQuestionsResponse;

public interface IRiskAssessmentService {
    RiskQuestionsResponse getQuestions();
    RiskAssessmentCalculateResponse calculateRiskProfile(RiskAssessmentCalculateRequest request);
}
