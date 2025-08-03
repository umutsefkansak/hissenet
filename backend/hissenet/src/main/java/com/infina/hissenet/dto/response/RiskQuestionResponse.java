package com.infina.hissenet.dto.response;

import java.util.List;

public record RiskQuestionResponse(
        Long id,
        String questionText,
        List<RiskOptionResponse> options
) {}