import { useState, useCallback } from 'react';

const useFormValidation = (initialData, validationRules) => {
    const [formData, setFormData] = useState(initialData);
    const [errors, setErrors] = useState({});

    const handleInputChange = useCallback((e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
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

        // Required fields validation
        if (validationRules.requiredFields) {
            validationRules.requiredFields.forEach(field => {
                if (!formData[field] || !formData[field].toString().trim()) {
                    newErrors[field] = 'Bu alan zorunludur';
                }
            });
        }


        if (formData.tcNumber && formData.tcNumber.length !== 11) {
            newErrors.tcNumber = 'TC Kimlik No 11 haneli olmalıdır';
        }


        if (formData.email && !/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = 'Geçerli bir e-posta adresi giriniz';
        }


        if (formData.phoneNumber && !/^[0-9]{10}$/.test(formData.phoneNumber)) {
            newErrors.phoneNumber = '10 haneli telefon numarası giriniz';
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


        if (formData.postalCode && !/^[0-9]{5}$/.test(formData.postalCode)) {
            newErrors.postalCode = '5 haneli posta kodu giriniz';
        }


        if (validationRules.customRules) {
            Object.keys(validationRules.customRules).forEach(field => {
                const rule = validationRules.customRules[field];
                if (formData[field] && !rule.test(formData[field])) {
                    newErrors[field] = rule.message;
                }
            });
        }

        if (formData.commissionRate) {
            const commission = parseFloat(formData.commissionRate);
            if (isNaN(commission)) {
                newErrors.commissionRate = 'Geçerli bir sayı giriniz';
            } else if (commission < 0) {
                newErrors.commissionRate = 'Komisyon oranı negatif olamaz';
            } else if (commission > 5) {
                newErrors.commissionRate = 'Komisyon oranı %5\'i geçemez';
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    }, [formData, validationRules]);

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

export default useFormValidation;