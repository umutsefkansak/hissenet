package com.infina.hissenet.exception.riskassessment;

public class IncompleteAssessmentException extends RiskAssessmentException {
    public IncompleteAssessmentException(String message) {
        super(message);
    }

    public IncompleteAssessmentException(int expectedQuestions, int providedAnswers) {
        super("Tüm sorular cevaplanmalıdır. Beklenen: " + expectedQuestions + ", Verilen: " + providedAnswers);
    }
}