package com.infina.hissenet.controller.doc;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.dto.request.RiskAssessmentCalculateRequest;
import com.infina.hissenet.dto.response.RiskAssessmentCalculateResponse;
import com.infina.hissenet.dto.response.RiskQuestionsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Risk Assessment", description = "Müşteri risk profili değerlendirme API'leri")
public interface RiskAssessmentControllerDoc {

    @Operation(summary = "Risk değerlendirme sorularını getir",
            description = "Müşteri kayıt sırasında kullanılacak risk profili sorularını döndürür")
    ApiResponse<RiskQuestionsResponse> getQuestions();

    @Operation(summary = "Risk profilini hesapla",
            description = "Verilen cevaplara göre müşterinin risk profilini hesaplar")
    ApiResponse<RiskAssessmentCalculateResponse> calculateRiskProfile(
            @Valid @RequestBody RiskAssessmentCalculateRequest request);
}