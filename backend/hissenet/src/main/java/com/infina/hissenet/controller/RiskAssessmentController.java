package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.RiskAssessmentControllerDoc;
import com.infina.hissenet.dto.request.RiskAssessmentCalculateRequest;
import com.infina.hissenet.dto.response.RiskAssessmentCalculateResponse;
import com.infina.hissenet.dto.response.RiskQuestionsResponse;
import com.infina.hissenet.service.abstracts.IRiskAssessmentService;
import com.infina.hissenet.utils.MessageUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/risk-assessment")
public class RiskAssessmentController implements RiskAssessmentControllerDoc {

    private final IRiskAssessmentService service;

    public RiskAssessmentController(IRiskAssessmentService service) {
        this.service = service;
    }

    @Override
    @GetMapping("/questions")
    public ApiResponse<RiskQuestionsResponse> getQuestions() {
        return ApiResponse.ok(MessageUtils.getMessage("risk.assessment.questions.retrieved.successfully"),
                service.getQuestions());
    }

    @Override
    @PostMapping("/calculate")
    public ApiResponse<RiskAssessmentCalculateResponse> calculateRiskProfile(
            @Valid @RequestBody RiskAssessmentCalculateRequest request) {
        return ApiResponse.ok(MessageUtils.getMessage("risk.assessment.profile.calculated.successfully"),
                service.calculateRiskProfile(request));
    }
}