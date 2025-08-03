package com.infina.hissenet.dto.response;

import java.util.List;

public record RiskQuestionsResponse(
        List<RiskQuestionResponse> questions
) {}