import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useCorporateFormValidation from '../../../hooks/CreateCustomer/useCorporateFormValidation';
import {
    CompanyInfoSection,
    AuthorizedPersonSection,
    CorporateContactInfoSection,
    FormActions
} from '../../../components/CreateCustomer';
import {
    createCorporateCustomer,
    mapCorporateFormDataToCustomerDto,
    handleCustomerApiError
} from '../../../server/customer';
import { createAddress, mapFormDataToAddressDto, handleAddressApiError } from '../../../server/address';
import styles from '../CreateCustomer.module.css';

const CorporateCustomer = () => {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);

    const initialFormData = {
        companyName: '',
        taxNumber: '',
        taxOffice: '',
        tradeRegistryNumber: '',
        establishmentDate: '',
        sector: '',
        website: '',

        authorizedPersonName: '',
        authorizedPersonTcNumber: '',
        authorizedPersonPhone: '',
        authorizedPersonEmail: '',

        phoneNumber: '',
        email: '',
        addressType: 'WORK',
        street: '',
        district: '',
        city: '',
        state: 'Türkiye',
        country: 'Türkiye',
        postalCode: '',
        commissionRate: '',

        nationality: 'T.C.'
    };

    const validationRules = {
        requiredFields: [
            'companyName', 'taxNumber', 'taxOffice', 'establishmentDate', 'sector',
            'authorizedPersonName', 'authorizedPersonTcNumber', 'authorizedPersonPhone', 'authorizedPersonEmail',
            'phoneNumber', 'email', 'addressType', 'street', 'city','commissionRate'
        ]
    };

    const {
        formData,
        errors,
        handleInputChange,
        validateForm,
        resetForm
    } = useCorporateFormValidation(initialFormData, validationRules);

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

        setIsLoading(true);

        try {


            const customerData = mapCorporateFormDataToCustomerDto(formData);
            console.log('Corporate Customer Data:', customerData);

            const customerResponse = await createCorporateCustomer(customerData);
            console.log('Corporate Customer Response:', customerResponse);

            if (customerResponse && customerResponse.data) {
                const customerId = customerResponse.data.id;
                console.log('Corporate Customer ID:', customerId);

                const addressData = mapFormDataToAddressDto(formData, customerId);
                console.log('Address Data:', addressData);
                const addressResponse = await createAddress(addressData);
                console.log('Address Response:', addressResponse);

                if (addressResponse && addressResponse.data) {
                    window.showToast && window.showToast('Kurumsal müşteri ve adres başarıyla kaydedildi!', 'success', 3000);

                    resetForm();

                    window.scrollTo({ top: 0, behavior: 'smooth' });
                } else {
                    window.showToast && window.showToast('Müşteri kaydedildi ancak adres kaydında sorun oluştu', 'warning', 4000);
                }
            } else {
                throw new Error('Beklenmeyen bir hata oluştu');
            }

        } catch (error) {
            console.error('Corporate Customer/Address creation error:', error);

            let errorMessage = handleCustomerApiError(error);

            if (error.message && error.message.includes('address')) {
                errorMessage = handleAddressApiError(error);
            }

            window.showToast && window.showToast(errorMessage, 'error', 4000);
        } finally {
            setIsLoading(false);
        }
    };

    const handleGoBack = () => {
        navigate(-1);
    };

    return (
        <div className={styles.newCustomer}>
            <div className={styles.createCustomerPageHeader}>
                <h1>YENİ KURUMSAL YATIRIMCI KAYIT EKRANI</h1>
                <p>Yeni kurumsal yatırımcı kaydı oluşturmak için aşağıdaki tüm gerekli alanların doldurulması gerekir</p>
            </div>

            <div className={styles.pageContent}>
                <form onSubmit={handleSubmit} className={styles.customerForm}>
                    <CompanyInfoSection
                        formData={formData}
                        handleInputChange={handleInputChange}
                        errors={errors}
                    />

                    <AuthorizedPersonSection
                        formData={formData}
                        handleInputChange={handleInputChange}
                        errors={errors}
                    />

                    <CorporateContactInfoSection
                        formData={formData}
                        handleInputChange={handleInputChange}
                        errors={errors}
                    />

                    <FormActions
                        onGoBack={handleGoBack}
                        onSubmit={handleSubmit}
                        isLoading={isLoading}
                    />
                </form>
            </div>
        </div>
    );
};

export default CorporateCustomer;