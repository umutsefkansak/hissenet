package com.infina.hissenet.exception.riskassessment;

import com.infina.hissenet.utils.MessageUtils;

public class InvalidAnswerException extends RiskAssessmentException {
    public InvalidAnswerException(String messageKey, Object... args) {
        super(MessageUtils.getMessage(messageKey, args));
    }

    public InvalidAnswerException(int questionNumber, int selectedIndex) {
        super(MessageUtils.getMessage("risk.assessment.invalid.answer", questionNumber, selectedIndex));
    }
}
