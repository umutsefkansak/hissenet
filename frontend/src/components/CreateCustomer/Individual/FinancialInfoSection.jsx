import React from 'react';
import FormInput from '../../Form/FormInput';
import { translateRiskProfile } from '../../../server/riskAssessment';
import FormSelect from "../../Form/FormSelect";
import { sectorOptions, professionOptions } from '../../../constants/CreateCustomer/formOptions';
import styles from '../../../pages/CreateCustomer/CreateCustomer.module.css';

const FinancialInfoSection = ({ formData, handleInputChange, errors, onRiskAnalysis, riskAssessmentResult }) => {
    return (
        <div className={styles.formSection}>
            <h3 className={styles.sectionTitle}>FİNANSAL BİLGİLER</h3>

            <div className={styles.formRow}>
                <FormSelect
                    label="Meslek"
                    name="profession"
                    value={formData.profession}
                    onChange={handleInputChange}
                    error={errors.profession}
                    options={professionOptions}
                    placeholder="Meslek seçiniz"
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

            <div className={styles.formRow}>
                <FormSelect
                    label="Sektör"
                    name="sector"
                    value={formData.sector}
                    onChange={handleInputChange}
                    error={errors.sector}
                    required
                    options={sectorOptions}
                    placeholder="Sektör seçiniz"
                />

                <FormInput
                    label="Komisyon Oranı (%)"
                    name="commissionRate"
                    type="number"
                    value={formData.commissionRate}
                    onChange={handleInputChange}
                    error={errors.commissionRate}
                    placeholder="Komisyon oranı giriniz"
                    step="0.01"
                    min="0"
                    max="100"
                />
            </div>

            <div className={styles.formRow}>
                <div className={styles.riskAnalysisContainer}>
                    <label className={styles.riskAnalysisLabel}>
                        Risk Analiz Testi <span className={styles.required}>*</span>
                    </label>
                    <button
                        type="button"
                        className={styles.riskAnalysisBtn}
                        onClick={onRiskAnalysis}
                    >
                        Risk Analiz Testi
                    </button>
                    {riskAssessmentResult && (
                        <div className={styles.riskResultDisplay}>
                            <span className={styles.riskResultLabel}>Sonuç:</span>
                            <span className={styles.riskResultValue}>
                                {translateRiskProfile(riskAssessmentResult.riskProfile)}
                            </span>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default FinancialInfoSection;