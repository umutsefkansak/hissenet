import React, { useState, useEffect } from 'react';
import { getRiskAssessmentQuestions, calculateRiskProfile, translateRiskProfile } from '../../server/riskAssessment';
import './RiskAssessmentModal.css';

const RiskAssessmentModal = ({ isOpen, onClose, onComplete }) => {
    const [questions, setQuestions] = useState([]);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [selectedAnswers, setSelectedAnswers] = useState([]);
    const [selectedOption, setSelectedOption] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isCalculating, setIsCalculating] = useState(false);
    const [result, setResult] = useState(null);
    const [phase, setPhase] = useState('questions'); // 'questions' | 'result'

    useEffect(() => {
        if (isOpen) {
            loadQuestions();
        }
    }, [isOpen]);

    const loadQuestions = async () => {
        try {
            setLoading(true);
            const response = await getRiskAssessmentQuestions();
            if (response && response.data && response.data.questions) {
                setQuestions(response.data.questions);
                setCurrentQuestionIndex(0);
                setSelectedAnswers([]);
                setSelectedOption(null);
                setPhase('questions');
                setResult(null);
            }
        } catch (error) {
            console.error('Failed to load questions:', error);
            window.showToast && window.showToast('Sorular yüklenirken hata oluştu!', 'error', 3000);
        } finally {
            setLoading(false);
        }
    };

    const handleOptionSelect = (optionIndex) => {
        setSelectedOption(optionIndex);
    };

    const handleNext = () => {
        if (selectedOption === null) {
            window.showToast && window.showToast('Lütfen bir seçenek seçiniz!', 'warning', 2000);
            return;
        }

        const newAnswers = [...selectedAnswers, selectedOption];
        setSelectedAnswers(newAnswers);

        if (currentQuestionIndex < questions.length - 1) {
            setCurrentQuestionIndex(currentQuestionIndex + 1);
            setSelectedOption(null);
        } else {
            // Son soru - sonuçları hesapla
            calculateResults(newAnswers);
        }
    };

    const handlePrevious = () => {
        if (currentQuestionIndex > 0) {
            setCurrentQuestionIndex(currentQuestionIndex - 1);
            const newAnswers = [...selectedAnswers];
            const previousAnswer = newAnswers.pop();
            setSelectedAnswers(newAnswers);
            setSelectedOption(previousAnswer);
        }
    };

    const calculateResults = async (answers) => {
        try {
            setIsCalculating(true);
            const response = await calculateRiskProfile(answers);

            if (response && response.data) {
                setResult(response.data);
                setPhase('result');
            }
        } catch (error) {
            console.error('Failed to calculate risk profile:', error);
            window.showToast && window.showToast('Risk profili hesaplanırken hata oluştu!', 'error', 3000);
        } finally {
            setIsCalculating(false);
        }
    };

    const handleComplete = () => {
        if (result && onComplete) {
            onComplete(result);
        }
        onClose();
    };

    const handleClose = () => {
        setQuestions([]);
        setCurrentQuestionIndex(0);
        setSelectedAnswers([]);
        setSelectedOption(null);
        setResult(null);
        setPhase('questions');
        onClose();
    };

    if (!isOpen) return null;

    const currentQuestion = questions[currentQuestionIndex];
    const progress = questions.length > 0 ? ((currentQuestionIndex + 1) / questions.length) * 100 : 0;

    return (
        <div className="risk-modal-overlay">
            <div className="risk-modal">
                <div className="risk-modal-header">
                    <h2 className="risk-modal-title">Risk Analizi Testi</h2>
                    <button className="risk-modal-close" onClick={handleClose}>
                        ✕
                    </button>
                </div>

                {loading ? (
                    <div className="risk-modal-loading">
                        <div className="spinner"></div>
                        <p>Sorular yükleniyor...</p>
                    </div>
                ) : phase === 'questions' ? (
                    <div className="risk-modal-content">
                        <div className="risk-modal-description">
                            Bu test, müşterinin yatırım profilini belirlemek ve ona uygun yatırım
                            araçlarını önermek için hazırlanmıştır. Lütfen soruları dikkate okuyarak
                            müşterinin seçtiği cevabı işaretleyiniz.
                        </div>

                        {questions.length > 0 && (
                            <>
                                <div className="risk-progress-container">
                                    <div className="risk-progress-info">
                    <span className="risk-progress-text">
                      {currentQuestionIndex + 1}/{questions.length}
                    </span>
                                    </div>
                                    <div className="risk-progress-bar">
                                        <div
                                            className="risk-progress-fill"
                                            style={{ width: `${progress}%` }}
                                        ></div>
                                    </div>
                                </div>

                                <div className="risk-question-container">
                                    <h3 className="risk-question-text">
                                        {currentQuestion?.questionText}
                                    </h3>

                                    <div className="risk-options-container">
                                        {currentQuestion?.options?.map((option, index) => (
                                            <div
                                                key={index}
                                                className={`risk-option ${selectedOption === index ? 'selected' : ''}`}
                                                onClick={() => handleOptionSelect(index)}
                                            >
                                                <div className="risk-option-radio">
                                                    <div className={`radio-dot ${selectedOption === index ? 'active' : ''}`}></div>
                                                </div>
                                                <span className="risk-option-text">{option.optionText}</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>

                                <div className="risk-modal-actions">
                                    <button
                                        className="risk-btn-secondary"
                                        onClick={handlePrevious}
                                        disabled={currentQuestionIndex === 0}
                                    >
                                        Önceki
                                    </button>
                                    <button
                                        className="risk-btn-primary"
                                        onClick={handleNext}
                                        disabled={isCalculating}
                                    >
                                        {isCalculating ? 'Hesaplanıyor...' :
                                            currentQuestionIndex === questions.length - 1 ? 'Sonraki' : 'Sonraki'}
                                    </button>
                                </div>
                            </>
                        )}
                    </div>
                ) : (
                    <div className="risk-modal-result">
                        <div className="risk-result-container">
                            <h3 className="risk-result-title">Risk Analizi Tamamlandı</h3>

                            <div className="risk-result-card">
                                <div className="risk-result-score">
                                    <span className="score-label">Toplam Puan:</span>
                                    <span className="score-value">{result?.totalScore}</span>
                                </div>

                                <div className="risk-result-profile">
                                    <span className="profile-label">Risk Profili:</span>
                                    <span className="profile-value">
                    {translateRiskProfile(result?.riskProfile)}
                  </span>
                                </div>

                                {result?.riskDescription && (
                                    <div className="risk-result-description">
                                        <p>{result.riskDescription}</p>
                                    </div>
                                )}
                            </div>

                            <div className="risk-result-actions">
                                <button className="risk-btn-primary" onClick={handleComplete}>
                                    Sonraki
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default RiskAssessmentModal;