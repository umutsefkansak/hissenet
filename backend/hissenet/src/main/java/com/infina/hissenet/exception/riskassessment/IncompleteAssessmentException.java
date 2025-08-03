package com.infina.hissenet.exception.riskassessment;

public class IncompleteAssessmentException extends RiskAssessmentException {
    public IncompleteAssessmentException(String message) {
        super(message);
    }

    public IncompleteAssessmentException(int expectedQuestions, int providedAnswers) {
        super("All questions must be answered. Expected: " + expectedQuestions + ", Provided: " + providedAnswers);
    }
}