import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useIndividualFormValidation from '../../../hooks/CreateCustomer/useIndividualFormValidation';
import {
    PersonalInfoSection,
    ContactInfoSection,
    FinancialInfoSection,
    FormActions
} from '../../../components/CreateCustomer';
import { RiskAssessmentModal } from '../../../components/RiskAssessment';
import { translateRiskProfile } from '../../../server/riskAssessment';
import {
    createIndividualCustomer,
    mapFormDataToCustomerDto,
    handleCustomerApiError
} from '../../../server/customer';
import { createAddress, mapFormDataToAddressDto, handleAddressApiError } from '../../../server/address';
import styles from '../CreateCustomer.module.css';

const IndividualCustomer = () => {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);
    const [isRiskModalOpen, setIsRiskModalOpen] = useState(false);
    const [riskAssessmentResult, setRiskAssessmentResult] = useState(null);

    const initialFormData = {
        firstName: '',
        lastName: '',
        motherName: '',
        fatherName: '',
        tcNumber: '',
        birthDate: '',
        birthPlace: '',
        educationLevel: '',
        gender: '',
        nationality: 'T.C.',

        phoneNumber: '',
        email: '',
        addressType: 'HOME',
        street: '',
        district: '',
        city: '',
        state: 'Türkiye',
        country: 'Türkiye',
        postalCode: '',

        profession: '',
        monthlyIncome: '',
        sector: '',
        commissionRate: ''
    };

    const validationRules = {
        requiredFields: [
            'firstName', 'lastName', 'tcNumber', 'birthDate', 'birthPlace',
            'educationLevel', 'gender', 'phoneNumber', 'email', 'addressType',
            'street', 'city', 'monthlyIncome','commissionRate'
        ]
    };

    const {
        formData,
        errors,
        handleInputChange,
        validateForm,
        resetForm
    } = useIndividualFormValidation(initialFormData, validationRules);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            const firstErrorField = Object.keys(errors)[0];
            if (firstErrorField) {
                const element = document.querySelector(`[name="${firstErrorField}"]`);
                if (element) {
                    element.scrollIntoView({
                        behavior: 'smooth',
                        block: 'center'
                    });
                    element.focus();
                }
            }
            window.showToast && window.showToast('Lütfen tüm zorunlu alanları doldurunuz', 'error', 3000);
            return;
        }

        if (!validateForm()) {
            return;
        }

        if (!riskAssessmentResult) {
            window.showToast && window.showToast('Lütfen önce risk analizi yapınız!', 'warning', 3000);
            return;
        }

        setIsLoading(true);

        try {
            const customerData = mapFormDataToCustomerDto(formData, riskAssessmentResult);
            console.log('Customer Data:', customerData);

            const customerResponse = await createIndividualCustomer(customerData);
            console.log('Customer Response:', customerResponse);

            if (customerResponse && customerResponse.data) {
                const customerId = customerResponse.data.id;
                console.log('Customer ID:', customerId);

                const addressData = mapFormDataToAddressDto(formData, customerId);
                console.log('Address Data:', addressData);
                const addressResponse = await createAddress(addressData);
                console.log('Address Response:', addressResponse);

                if (addressResponse && addressResponse.data) {
                    window.showToast && window.showToast('Bireysel müşteri ve adres başarıyla kaydedildi!', 'success', 3000);

                    resetForm();
                    setRiskAssessmentResult(null);

                    window.scrollTo({ top: 0, behavior: 'smooth' });
                } else {
                    window.showToast && window.showToast('Müşteri kaydedildi ancak adres kaydında sorun oluştu', 'warning', 4000);
                }
            } else {
                throw new Error('Beklenmeyen bir hata oluştu');
            }

        } catch (error) {
            console.error('Customer/Address creation error:', error);

            let errorMessage = handleCustomerApiError(error);


            if (error.message && error.message.includes('address')) {
                errorMessage = handleAddressApiError(error);
            }

            window.showToast && window.showToast(errorMessage, 'error', 4000);
        } finally {
            setIsLoading(false);
        }
    };

    const handleRiskAnalysis = () => {
        setIsRiskModalOpen(true);
    };

    const handleRiskAssessmentComplete = (result) => {
        setRiskAssessmentResult(result);
        window.showToast && window.showToast('Risk analizi tamamlandı!', 'success', 2000);
    };

    const handleRiskModalClose = () => {
        setIsRiskModalOpen(false);
    };

    const handleGoBack = () => {
        navigate(-1);
    };

    return (
        <div className={styles.newCustomer}>
            <div className={styles.createCustomerPageHeader}>
                <h1>YENİ BİREYSEL YATIRIMCI KAYIT EKRANI</h1>
                <p>Yeni bireysel yatırımcı kaydı oluşturmak için aşağıdaki tüm gerekli alanların doldurulması gerekir</p>
            </div>

            <div className={styles.pageContent}>
                <form onSubmit={handleSubmit} className={styles.customerForm}>
                    <PersonalInfoSection
                        formData={formData}
                        handleInputChange={handleInputChange}
                        errors={errors}
                    />

                    <ContactInfoSection
                        formData={formData}
                        handleInputChange={handleInputChange}
                        errors={errors}
                    />

                    <FinancialInfoSection
                        formData={formData}
                        handleInputChange={handleInputChange}
                        errors={errors}
                        onRiskAnalysis={handleRiskAnalysis}
                        riskAssessmentResult={riskAssessmentResult}
                    />

                    <FormActions
                        onGoBack={handleGoBack}
                        onSubmit={handleSubmit}
                        isLoading={isLoading}
                    />
                </form>
            </div>

            <RiskAssessmentModal
                isOpen={isRiskModalOpen}
                onClose={handleRiskModalClose}
                onComplete={handleRiskAssessmentComplete}
            />
        </div>
    );
};

export default IndividualCustomer;