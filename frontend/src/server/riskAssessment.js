import api from './api';


export const getRiskAssessmentQuestions = async () => {
    try {
        const response = await api.get('/risk-assessment/questions');
        return response.data;
    } catch (error) {
        console.error('Risk assessment questions error:', error);
        throw error;
    }
};


export const calculateRiskProfile = async (selectedOptionIndexes) => {
    try {
        const response = await api.post('/risk-assessment/calculate', {
            selectedOptionIndexes
        });
        return response.data;
    } catch (error) {
        console.error('Risk profile calculation error:', error);
        throw error;
    }
};


export const translateRiskProfile = (riskProfile) => {
    const translations = {
        'CONSERVATIVE': 'Düşük Risk',
        'MODERATE': 'Orta Risk',
        'AGGRESSIVE': 'Yüksek Risk',
        'VERY_AGGRESSIVE': 'Çok Yüksek Risk'
    };
    return translations[riskProfile] || riskProfile;
};