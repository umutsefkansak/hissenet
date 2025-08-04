package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RiskAssessmentCalculateRequest(
        @NotNull List<Integer> selectedOptionIndexes
) {}