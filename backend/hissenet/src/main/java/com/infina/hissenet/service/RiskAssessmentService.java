package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.RiskAssessmentCalculateRequest;
import com.infina.hissenet.dto.response.RiskAssessmentCalculateResponse;
import com.infina.hissenet.dto.response.RiskQuestionsResponse;
import com.infina.hissenet.entity.RiskQuestion;
import com.infina.hissenet.entity.RiskOption;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.exception.riskassessment.IncompleteAssessmentException;
import com.infina.hissenet.exception.riskassessment.InvalidAnswerException;
import com.infina.hissenet.mapper.RiskAssessmentMapper;
import com.infina.hissenet.repository.RiskQuestionRepository;
import com.infina.hissenet.service.abstracts.IRiskAssessmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RiskAssessmentService implements IRiskAssessmentService {

    private final RiskQuestionRepository questionRepository;
    private final RiskAssessmentMapper mapper;

    public RiskAssessmentService(RiskQuestionRepository questionRepository,
                                 RiskAssessmentMapper mapper) {
        this.questionRepository = questionRepository;
        this.mapper = mapper;
    }

    @Override
    public RiskQuestionsResponse getQuestions() {
        List<RiskQuestion> questions = questionRepository.findAllByOrderByOrderIndexAsc();
        return mapper.toQuestionsResponse(questions);
    }

    @Override
    public RiskAssessmentCalculateResponse calculateRiskProfile(RiskAssessmentCalculateRequest request) {
        List<RiskQuestion> questions = questionRepository.findAllByOrderByOrderIndexAsc();
        List<Integer> selectedIndexes = request.selectedOptionIndexes();

        if (selectedIndexes.size() != questions.size()) {
            throw new IncompleteAssessmentException(questions.size(), selectedIndexes.size());
        }

        int totalScore = 0;
        for (int i = 0; i < selectedIndexes.size(); i++) {
            int selectedIndex = selectedIndexes.get(i);
            List<RiskOption> options = questions.get(i).getOptions();

            if (selectedIndex < 0 || selectedIndex >= options.size()) {
                throw new InvalidAnswerException(i + 1, selectedIndex);
            }

            totalScore += options.get(selectedIndex).getScore();
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