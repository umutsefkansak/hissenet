package com.infina.hissenet.exception.riskassessment;

import com.infina.hissenet.utils.MessageUtils;

public class IncompleteAssessmentException extends RiskAssessmentException {
    public IncompleteAssessmentException(String messageKey, Object... args) {
        super(MessageUtils.getMessage(messageKey, args));
    }

    public IncompleteAssessmentException(int expectedQuestions, int providedAnswers) {
        super(MessageUtils.getMessage("risk.assessment.incomplete", expectedQuestions, providedAnswers));
    }
}