package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.RiskAssessmentCalculateRequest;
import com.infina.hissenet.dto.response.RiskAssessmentCalculateResponse;
import com.infina.hissenet.dto.response.RiskQuestionsResponse;

/**
 * Interface for risk assessment operations.
 * Provides methods to retrieve risk questions and calculate the user's risk profile.
 * @author Umut Sefkan SAK
 * @version 1.0
 */
public interface IRiskAssessmentService {


    /**
     * Retrieves all risk assessment questions along with their options.
     *
     * @return a response containing the list of questions and options
     */
    RiskQuestionsResponse getQuestions();

    /**
     * Calculates the risk profile based on the answers provided in the request.
     *
     * @param request contains the user's selected answers
     * @return the calculated risk profile
     */
    RiskAssessmentCalculateResponse calculateRiskProfile(RiskAssessmentCalculateRequest request);
}
