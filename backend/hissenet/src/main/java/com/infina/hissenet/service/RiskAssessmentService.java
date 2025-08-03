package com.infina.hissenet.service;

import com.infina.hissenet.config.RiskAssessmentConfig;
import com.infina.hissenet.dto.request.RiskAssessmentCalculateRequest;
import com.infina.hissenet.dto.response.RiskAssessmentCalculateResponse;
import com.infina.hissenet.dto.response.RiskQuestionsResponse;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.service.abstracts.IRiskAssessmentService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RiskAssessmentService implements IRiskAssessmentService {

    public RiskQuestionsResponse getQuestions() {
        return new RiskQuestionsResponse(RiskAssessmentConfig.QUESTIONS);
    }

    public RiskAssessmentCalculateResponse calculateRiskProfile(RiskAssessmentCalculateRequest request) {

        List<Integer> selectedIndexes = request.selectedOptionIndexes();


        if (selectedIndexes.size() != RiskAssessmentConfig.QUESTIONS.size()) {
            throw new IllegalArgumentException("Tüm sorular cevaplanmalıdır");
        }

        int totalScore = 0;
        for (int i = 0; i < selectedIndexes.size(); i++) {
            int selectedIndex = selectedIndexes.get(i);
            List<RiskAssessmentConfig.RiskOption> options = RiskAssessmentConfig.QUESTIONS.get(i).options();


            if (selectedIndex < 0 || selectedIndex >= options.size()) {
                throw new IllegalArgumentException("Soru " + (i+1) + " için geçersiz seçenek: " + selectedIndex);
            }

            totalScore += options.get(selectedIndex).score();
        }


        RiskProfile riskProfile = determineRiskProfile(totalScore);

        return new RiskAssessmentCalculateResponse(
                totalScore,
                riskProfile,
                getRiskDescription(riskProfile)
        );
    }

    private RiskProfile determineRiskProfile(int totalScore) {

        if (totalScore <= 8) return RiskProfile.CONSERVATIVE;
        else if (totalScore <= 12) return RiskProfile.MODERATE;
        else if (totalScore <= 16) return RiskProfile.AGGRESSIVE;
        else return RiskProfile.VERY_AGGRESSIVE;
    }

    private String getRiskDescription(RiskProfile profile) {
        return switch (profile) {
            case CONSERVATIVE -> "Muhafazakar yatırımcı profili. Düşük riskli yatırımlar önerilir.";
            case MODERATE -> "Orta riskli yatırımcı profili. Dengeli portföy önerilir.";
            case AGGRESSIVE -> "Agresif yatırımcı profili. Yüksek getirili yatırımlar yapabilir.";
            case VERY_AGGRESSIVE -> "Çok agresif yatırımcı profili. En yüksek riskli yatırımlar yapabilir.";
        };
    }
}