import React from 'react';
import FormInput from '../Form/FormInput';
import { translateRiskProfile } from '../../server/riskAssessment';

const FinancialInfoSection = ({ formData, handleInputChange, errors, onRiskAnalysis, riskAssessmentResult }) => {
    return (
        <div className="form-section">
            <h3 className="section-title">FİNANSAL BİLGİLER</h3>

            <div className="form-row">
                <FormInput
                    label="Meslek"
                    name="profession"
                    value={formData.profession}
                    onChange={handleInputChange}
                    placeholder="Meslek"
                />

                <FormInput
                    label="Aylık Gelir (TL)"
                    name="monthlyIncome"
                    type="number"
                    value={formData.monthlyIncome}
                    onChange={handleInputChange}
                    error={errors.monthlyIncome}
                    required
                    placeholder="Aylık Gelir (TL)"
                    min="0"
                />
            </div>

            <div className="form-row">
                <FormInput
                    label="Sektör"
                    name="sector"
                    value={formData.sector}
                    onChange={handleInputChange}
                    placeholder="Sektör"
                />

                <FormInput
                    label="Komisyon Oranı (%)"
                    name="commissionRate"
                    type="number"
                    value={formData.commissionRate}
                    onChange={handleInputChange}
                    placeholder="Varsayılan oran kullanılacak"
                    step="0.01"
                    min="0"
                    max="100"
                />
            </div>

            <div className="form-row">
                <div className="risk-analysis-container">
                    <button
                        type="button"
                        className="risk-analysis-btn"
                        onClick={onRiskAnalysis}
                    >
                        Risk Analizi
                    </button>
                    {riskAssessmentResult ? (
                        <div className="risk-result-display">
                            <span className="risk-result-label">Sonuç:</span>
                            <span className="risk-result-value">
                {translateRiskProfile(riskAssessmentResult.riskProfile)}
              </span>
                        </div>
                    ) : (
                        <div className="risk-warning-display">
              <span className="risk-warning-text">
                Risk analizi yapılması zorunludur
              </span>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default FinancialInfoSection;