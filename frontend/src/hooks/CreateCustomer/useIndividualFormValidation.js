import { useState, useCallback } from 'react';
import { validationRules, cleanInput, fieldTypes } from '../../utils/CreateCustomer/validationRules';


const useIndividualFormValidation = (initialData, validationConfig) => {
    const [formData, setFormData] = useState(initialData);
    const [errors, setErrors] = useState({});

    const handleInputChange = useCallback((e) => {
        const { name, value } = e.target;
        
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
                    return;
                }
            });
        }

        ['firstName', 'lastName', 'motherName', 'fatherName'].forEach(field => {
            if (formData[field] && !validationRules.name.pattern.test(formData[field])) {
                newErrors[field] = validationRules.name.message;
            }
        });

        if (formData.tcNumber && !validationRules.tcNumber.pattern.test(formData.tcNumber)) {
            newErrors.tcNumber = validationRules.tcNumber.message;
        }

        if (formData.email && !validationRules.email.pattern.test(formData.email)) {
            newErrors.email = validationRules.email.message;
        }

        if (formData.phoneNumber && !validationRules.phone.pattern.test(formData.phoneNumber)) {
            newErrors.phoneNumber = validationRules.phone.message;
        }

        if (formData.birthDate) {
            const birthDate = new Date(formData.birthDate);
            const today = new Date();
            let age = today.getFullYear() - birthDate.getFullYear();
            const monthDiff = today.getMonth() - birthDate.getMonth();

            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }

            if (age < 18) {
                newErrors.birthDate = 'Yaşınız 18\'den büyük olmalıdır';
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
            }
            // else if (commission > 1) {
            //     newErrors.commissionRate = 'Komisyon oranı %100\'den fazla olamaz';
            // }
        }

        if (formData.profession && !validationRules.name.pattern.test(formData.profession)) {
            newErrors.profession = 'Meslek alanında sadece harf karakterler kullanılabilir';
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

export default useIndividualFormValidation;