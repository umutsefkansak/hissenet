import { useState, useCallback } from 'react';
import { validationRules, cleanInput, fieldTypes } from '../../utils/CreateCustomer/validationRules';

const useCorporateFormValidation = (initialData, validationConfig) => {
    const [formData, setFormData] = useState(initialData);
    const [errors, setErrors] = useState({});

    const handleInputChange = useCallback((e) => {
        const { name, value } = e.target;
        
        // Input'u temizle
        const cleanedValue = cleanInput(value, fieldTypes[name]);

        setFormData(prev => ({
            ...prev,
            [name]: cleanedValue  
        }));

        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    }, [errors]);

    const validateForm = useCallback(() => {
        const newErrors = {};

        if (validationConfig.requiredFields) {
            validationConfig.requiredFields.forEach(field => {
                if (!formData[field] || !formData[field].toString().trim()) {
                    newErrors[field] = 'Bu alan zorunludur';
                }
            });
        }

        if (formData.companyName && /^\d+$/.test(formData.companyName)) {
            newErrors.companyName = 'Şirket adı sadece sayılardan oluşamaz';
        }

        if (formData.authorizedPersonName && !validationRules.name.pattern.test(formData.authorizedPersonName)) {
            newErrors.authorizedPersonName = validationRules.name.message;
        }

        if (formData.taxNumber && !validationRules.taxNumber.pattern.test(formData.taxNumber)) {
            newErrors.taxNumber = validationRules.taxNumber.message;
        }

        if (formData.authorizedPersonTcNumber && !validationRules.tcNumber.pattern.test(formData.authorizedPersonTcNumber)) {
            newErrors.authorizedPersonTcNumber = validationRules.tcNumber.message;
        }

        if (formData.email && !validationRules.email.pattern.test(formData.email)) {
            newErrors.email = validationRules.email.message;
        }

        if (formData.authorizedPersonEmail && !validationRules.email.pattern.test(formData.authorizedPersonEmail)) {
            newErrors.authorizedPersonEmail = validationRules.email.message;
        }

        if (formData.phoneNumber && !validationRules.phone.pattern.test(formData.phoneNumber)) {
            newErrors.phoneNumber = validationRules.phone.message;
        }

        if (formData.authorizedPersonPhone && !validationRules.phone.pattern.test(formData.authorizedPersonPhone)) {
            newErrors.authorizedPersonPhone = validationRules.phone.message;
        }

        if (formData.website && !/^https?:\/\/.+\..+/.test(formData.website)) {
            newErrors.website = 'Geçerli bir web sitesi adresi giriniz (http:// veya https:// ile başlamalı)';
        }

        if (formData.establishmentDate) {
            const establishmentDate = new Date(formData.establishmentDate);
            const today = new Date();

            if (establishmentDate > today) {
                newErrors.establishmentDate = 'Kuruluş tarihi bugünden sonra olamaz';
            }
        }

        if (formData.postalCode && !validationRules.postalCode.pattern.test(formData.postalCode)) {
            newErrors.postalCode = validationRules.postalCode.message;
        }

        if (formData.commissionRate) {
            const commission = parseFloat(formData.commissionRate);
            if (isNaN(commission)) {
                newErrors.commissionRate = 'Geçerli bir sayı giriniz';
            } else if (commission < 0) {
                newErrors.commissionRate = 'Komisyon oranı negatif olamaz';
            }else if (commission > 1) {
                newErrors.commissionRate = 'Komisyon oranı %100\'den fazla olamaz';
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    }, [formData, validationConfig]);

    const resetForm = useCallback(() => {
        setFormData(initialData);
        setErrors({});
    }, [initialData]);

    const updateFormData = useCallback((data) => {
        setFormData(prev => ({ ...prev, ...data }));
    }, []);

    return {
        formData,
        errors,
        handleInputChange,
        validateForm,
        resetForm,
        updateFormData,
        setFormData
    };
};

export default useCorporateFormValidation;