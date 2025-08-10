package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.RiskAssessmentCalculateRequest;
import com.infina.hissenet.dto.response.RiskAssessmentCalculateResponse;
import com.infina.hissenet.dto.response.RiskQuestionsResponse;
import com.infina.hissenet.entity.RiskOption;
import com.infina.hissenet.entity.RiskQuestion;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.exception.riskassessment.IncompleteAssessmentException;
import com.infina.hissenet.exception.riskassessment.InvalidAnswerException;
import com.infina.hissenet.mapper.RiskAssessmentMapper;
import com.infina.hissenet.repository.RiskQuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Risk Assessment Service Tests")
class RiskAssessmentServiceTest {

    @Mock
    private RiskQuestionRepository questionRepository;

    @Mock
    private RiskAssessmentMapper mapper;

    @InjectMocks
    private RiskAssessmentService riskAssessmentService;

    private List<RiskQuestion> mockQuestions;

    @BeforeEach
    void setUp() {
        mockQuestions = createMockQuestions();
    }

    @Test
    @DisplayName("Should return questions response when getting questions")
    void getQuestions_ShouldReturnQuestionsResponse() {
        // Given
        RiskQuestionsResponse expectedResponse = new RiskQuestionsResponse(List.of());
        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);
        when(mapper.toQuestionsResponse(mockQuestions)).thenReturn(expectedResponse);

        // When
        RiskQuestionsResponse actualResponse = riskAssessmentService.getQuestions();

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(questionRepository).findAllByOrderByOrderIndexAsc();
        verify(mapper).toQuestionsResponse(mockQuestions);
    }

    @Test
    @DisplayName("Should calculate CONSERVATIVE risk profile for minimum scores")
    void calculateRiskProfile_ShouldReturnConservativeProfile_WhenMinimumScores() {
        // Given - En düşük skorlar (1+1+1+1+1 = 5)
        List<Integer> selectedIndexes = Arrays.asList(3, 0, 0, 0, 0); // 50+ yaş, İlköğretim, Kısa vadeli, Güvenli, Yeni başlıyor
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When
        RiskAssessmentCalculateResponse response = riskAssessmentService.calculateRiskProfile(request);

        // Then
        assertThat(response.totalScore()).isEqualTo(5);
        assertThat(response.riskProfile()).isEqualTo(RiskProfile.CONSERVATIVE);
        assertThat(response.riskDescription()).isEqualTo("Düşük riskli yatırımcı profili. Düşük riskli yatırımlar önerilir.");
    }

    @Test
    @DisplayName("Should calculate CONSERVATIVE risk profile for score 8")
    void calculateRiskProfile_ShouldReturnConservativeProfile_WhenScore8() {
        // Given - Skor 8 (2+2+1+2+1 = 8)
        List<Integer> selectedIndexes = Arrays.asList(2, 1, 0, 1, 0); // 35-50 yaş, Lise, Kısa vadeli, Orta risk, Yeni başlıyor
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When
        RiskAssessmentCalculateResponse response = riskAssessmentService.calculateRiskProfile(request);

        // Then
        assertThat(response.totalScore()).isEqualTo(8);
        assertThat(response.riskProfile()).isEqualTo(RiskProfile.CONSERVATIVE);
    }

    @Test
    @DisplayName("Should calculate MODERATE risk profile for score 9")
    void calculateRiskProfile_ShouldReturnModerateProfile_WhenScore9() {
        // Given - Skor 9 (2+2+2+2+1 = 9)
        List<Integer> selectedIndexes = Arrays.asList(2, 1, 1, 1, 0); // 35-50 yaş, Lise, Orta vadeli, Orta risk, Yeni başlıyor
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When
        RiskAssessmentCalculateResponse response = riskAssessmentService.calculateRiskProfile(request);

        // Then
        assertThat(response.totalScore()).isEqualTo(9);
        assertThat(response.riskProfile()).isEqualTo(RiskProfile.MODERATE);
        assertThat(response.riskDescription()).isEqualTo("Orta riskli yatırımcı profili. Dengeli portföy önerilir.");
    }

    @Test
    @DisplayName("Should calculate MODERATE risk profile for score 12")
    void calculateRiskProfile_ShouldReturnModerateProfile_WhenScore12() {
        // Given - Skor 12 (3+2+3+2+2 = 12)
        List<Integer> selectedIndexes = Arrays.asList(0, 1, 2, 1, 1); // 25 yaş altı, Lise, Uzun vadeli, Orta risk, Temel bilgi
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When
        RiskAssessmentCalculateResponse response = riskAssessmentService.calculateRiskProfile(request);

        // Then
        assertThat(response.totalScore()).isEqualTo(12);
        assertThat(response.riskProfile()).isEqualTo(RiskProfile.MODERATE);
    }

    @Test
    @DisplayName("Should calculate AGGRESSIVE risk profile for score 13")
    void calculateRiskProfile_ShouldReturnAggressiveProfile_WhenScore13() {
        // Given - Skor 13 (4+3+2+2+2 = 13)
        List<Integer> selectedIndexes = Arrays.asList(1, 2, 1, 1, 1); // 25-35 yaş, Üniversite, Orta vadeli, Orta risk, Temel bilgi
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When
        RiskAssessmentCalculateResponse response = riskAssessmentService.calculateRiskProfile(request);

        // Then
        assertThat(response.totalScore()).isEqualTo(13);
        assertThat(response.riskProfile()).isEqualTo(RiskProfile.AGGRESSIVE);
        assertThat(response.riskDescription()).isEqualTo("Yüksek riskli yatırımcı profili. Yüksek getirili yatırımlar yapabilir.");
    }

    @Test
    @DisplayName("Should calculate AGGRESSIVE risk profile for score 16")
    void calculateRiskProfile_ShouldReturnAggressiveProfile_WhenScore16() {
        // Given - Skor 16 (4+4+3+3+2 = 16)
        List<Integer> selectedIndexes = Arrays.asList(1, 3, 2, 2, 1); // 25-35 yaş, Lisansüstü, Uzun vadeli, Yüksek risk, Temel bilgi
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When
        RiskAssessmentCalculateResponse response = riskAssessmentService.calculateRiskProfile(request);

        // Then
        assertThat(response.totalScore()).isEqualTo(16);
        assertThat(response.riskProfile()).isEqualTo(RiskProfile.AGGRESSIVE);
    }

    @Test
    @DisplayName("Should calculate VERY_AGGRESSIVE risk profile for score 17")
    void calculateRiskProfile_ShouldReturnVeryAggressiveProfile_WhenScore17() {
        // Given - Skor 17 (4+4+3+3+3 = 17)
        List<Integer> selectedIndexes = Arrays.asList(1, 3, 2, 2, 2); // 25-35 yaş, Lisansüstü, Uzun vadeli, Yüksek risk, Orta bilgi
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When
        RiskAssessmentCalculateResponse response = riskAssessmentService.calculateRiskProfile(request);

        // Then
        assertThat(response.totalScore()).isEqualTo(17);
        assertThat(response.riskProfile()).isEqualTo(RiskProfile.VERY_AGGRESSIVE);
        assertThat(response.riskDescription()).isEqualTo("Çok yüksek riskli yatırımcı profili. En yüksek riskli yatırımlar yapabilir.");
    }

    @Test
    @DisplayName("Should calculate VERY_AGGRESSIVE risk profile for maximum scores")
    void calculateRiskProfile_ShouldReturnVeryAggressiveProfile_WhenMaximumScores() {
        // Given - En yüksek skorlar (4+4+4+4+4 = 20)
        List<Integer> selectedIndexes = Arrays.asList(1, 3, 3, 3, 3); // 25-35 yaş, Lisansüstü, Çok uzun vadeli, Maksimum risk, İleri bilgi
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When
        RiskAssessmentCalculateResponse response = riskAssessmentService.calculateRiskProfile(request);

        // Then
        assertThat(response.totalScore()).isEqualTo(20);
        assertThat(response.riskProfile()).isEqualTo(RiskProfile.VERY_AGGRESSIVE);
    }

    @Test
    @DisplayName("Should throw IncompleteAssessmentException when not all questions are answered")
    void calculateRiskProfile_ShouldThrowIncompleteAssessmentException_WhenNotAllQuestionsAnswered() {
        // Given
        List<Integer> selectedIndexes = Arrays.asList(0, 1, 2); // Sadece 3 cevap, 5 soru var
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When & Then
        assertThatThrownBy(() -> riskAssessmentService.calculateRiskProfile(request))
                .isInstanceOf(IncompleteAssessmentException.class);
    }

    @Test
    @DisplayName("Should throw IncompleteAssessmentException when too many answers provided")
    void calculateRiskProfile_ShouldThrowIncompleteAssessmentException_WhenTooManyAnswers() {
        // Given
        List<Integer> selectedIndexes = Arrays.asList(0, 1, 2, 3, 2, 1); // 6 cevap, 5 soru var
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When & Then
        assertThatThrownBy(() -> riskAssessmentService.calculateRiskProfile(request))
                .isInstanceOf(IncompleteAssessmentException.class);
    }

    @Test
    @DisplayName("Should throw InvalidAnswerException when negative option index provided")
    void calculateRiskProfile_ShouldThrowInvalidAnswerException_WhenNegativeIndex() {
        // Given
        List<Integer> selectedIndexes = Arrays.asList(0, -1, 2, 3, 1); // Negatif index
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When & Then
        assertThatThrownBy(() -> riskAssessmentService.calculateRiskProfile(request))
                .isInstanceOf(InvalidAnswerException.class);
    }

    @Test
    @DisplayName("Should throw InvalidAnswerException when option index is out of bounds")
    void calculateRiskProfile_ShouldThrowInvalidAnswerException_WhenIndexOutOfBounds() {
        // Given
        List<Integer> selectedIndexes = Arrays.asList(0, 1, 2, 5, 1); // Index 5, ama sadece 4 seçenek var (0-3)
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When & Then
        assertThatThrownBy(() -> riskAssessmentService.calculateRiskProfile(request))
                .isInstanceOf(InvalidAnswerException.class);
    }

    @Test
    @DisplayName("Should handle edge case with exactly 4 options per question")
    void calculateRiskProfile_ShouldWork_WithValidIndexes() {
        // Given - Her soru için geçerli maksimum index (3)
        List<Integer> selectedIndexes = Arrays.asList(3, 3, 3, 3, 3);
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When
        RiskAssessmentCalculateResponse response = riskAssessmentService.calculateRiskProfile(request);

        // Then
        assertThat(response.totalScore()).isEqualTo(17); // 1+4+4+4+4 = 17 (50+ yaş=1, diğerleri 4)
        assertThat(response.riskProfile()).isEqualTo(RiskProfile.VERY_AGGRESSIVE);
    }

    @Test
    @DisplayName("Should verify repository is called with correct method")
    void calculateRiskProfile_ShouldCallRepositoryWithCorrectMethod() {
        // Given
        List<Integer> selectedIndexes = Arrays.asList(0, 1, 2, 3, 1);
        RiskAssessmentCalculateRequest request = new RiskAssessmentCalculateRequest(selectedIndexes);

        when(questionRepository.findAllByOrderByOrderIndexAsc()).thenReturn(mockQuestions);

        // When
        riskAssessmentService.calculateRiskProfile(request);

        // Then
        verify(questionRepository, times(1)).findAllByOrderByOrderIndexAsc();
    }

    private List<RiskQuestion> createMockQuestions() {
        // Soru 1: Yaş aralığınızı seçiniz
        RiskQuestion ageQuestion = new RiskQuestion("Yaş aralığınızı seçiniz:", 1);
        ageQuestion.setOptions(Arrays.asList(
                new RiskOption("25 yaş altı", 3, 1),
                new RiskOption("25-35 arası", 4, 2),
                new RiskOption("35-50 arası", 2, 3),
                new RiskOption("50 yaş üstü", 1, 4)
        ));

        // Soru 2: Eğitim durumunuzu seçiniz
        RiskQuestion educationQuestion = new RiskQuestion("Eğitim durumunuzu seçiniz:", 2);
        educationQuestion.setOptions(Arrays.asList(
                new RiskOption("İlköğretim", 1, 1),
                new RiskOption("Lise", 2, 2),
                new RiskOption("Üniversite", 3, 3),
                new RiskOption("Lisansüstü", 4, 4)
        ));

        // Soru 3: Ne kadar süreli yatırım yapmayı düşünüyorsunuz?
        RiskQuestion durationQuestion = new RiskQuestion("Ne kadar süreli yatırım yapmayı düşünüyorsunuz?", 3);
        durationQuestion.setOptions(Arrays.asList(
                new RiskOption("Kısa vadeli (1 yıldan az)", 1, 1),
                new RiskOption("Orta vadeli (1-3 yıl)", 2, 2),
                new RiskOption("Uzun vadeli (3-10 yıl)", 3, 3),
                new RiskOption("Çok uzun vadeli (10+ yıl)", 4, 4)
        ));

        // Soru 4: Yatırım yaparken hangisi sizi daha iyi tanımlar?
        RiskQuestion riskToleranceQuestion = new RiskQuestion("Yatırım yaparken hangisi sizi daha iyi tanımlar?", 4);
        riskToleranceQuestion.setOptions(Arrays.asList(
                new RiskOption("Güvenli, düşük getirili yatırımları tercih ederim", 1, 1),
                new RiskOption("Orta düzeyde risk alabilirim", 2, 2),
                new RiskOption("Yüksek getiri için risk alabilirim", 3, 3),
                new RiskOption("Çok yüksek risk alarak maksimum getiri isterim", 4, 4)
        ));

        // Soru 5: Finansal bilginiz ne durumda?
        RiskQuestion knowledgeQuestion = new RiskQuestion("Finansal bilginiz ne durumda?", 5);
        knowledgeQuestion.setOptions(Arrays.asList(
                new RiskOption("Yeni başlıyorum", 1, 1),
                new RiskOption("Temel bilgilerim var", 2, 2),
                new RiskOption("Orta düzeyde bilgiliyim", 3, 3),
                new RiskOption("İleri düzeyde bilgiliyim", 4, 4)
        ));

        return Arrays.asList(ageQuestion, educationQuestion, durationQuestion, riskToleranceQuestion, knowledgeQuestion);
    }
}