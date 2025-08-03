package com.infina.hissenet.exception.riskassessment;

public class InvalidAnswerException extends RiskAssessmentException {
    public InvalidAnswerException(String message) {
        super(message);
    }

    public InvalidAnswerException(int questionNumber, int selectedIndex) {
        super("Invalid option for question " + questionNumber + ": " + selectedIndex);
    }
}
