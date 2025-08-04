package com.infina.hissenet.dto.response;

import java.util.List;

public record RiskQuestionResponse(
        String questionText,
        List<RiskOptionResponse> options
) {}