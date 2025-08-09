import { useState, useCallback, useEffect } from 'react';
import { validationRules, cleanInput, fieldTypes } from '../../utils/EmployeeManagement/validationRules';

const useEmployeeFormValidation = (initialData, validationConfig, isEdit = false) => {
    const [formData, setFormData] = useState(initialData);
    const [errors, setErrors] = useState({});

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        const fieldType = fieldTypes[name];
        const cleanedValue = fieldType ? cleanInput(value, fieldType) : value;

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
    };

    const handleRoleChange = (e) => {
        const { value, checked } = e.target;
        const roleId = parseInt(value, 10);

        console.log('Role değişikliği:', { roleId, checked, currentRoles: formData.roleIds });

        setFormData(prev => {
            const currentRoles = prev.roleIds || [];
            const newRoles = checked
                ? currentRoles.includes(roleId)
                    ? currentRoles
                    : [...currentRoles, roleId]
                : currentRoles.filter(id => id !== roleId);

            console.log('Yeni roller:', newRoles);
            return {
                ...prev,
                roleIds: newRoles
            };
        });
    };

    const toggleRole = (roleId) => {
        setFormData(prev => {
            const currentRoles = prev.roleIds || [];
            const isSelected = currentRoles.includes(roleId);
            const newRoles = isSelected
                ? currentRoles.filter(id => id !== roleId)
                : [...currentRoles, roleId];

            console.log('Toggle role:', { roleId, isSelected, newRoles });
            return {
                ...prev,
                roleIds: newRoles
            };
        });
    };

    const validateForm = useCallback(() => {
        const newErrors = {};

        if (validationConfig.requiredFields) {
            validationConfig.requiredFields.forEach(field => {
                if (!formData[field] || !formData[field].toString().trim()) {
                    newErrors[field] = 'Bu alan zorunludur';
                }
            });
        }

        if (formData.firstName && !validationRules.name.pattern.test(formData.firstName)) {
            newErrors.firstName = validationRules.name.message;
        } else if (formData.firstName && (formData.firstName.length < 2 || formData.firstName.length > 50)) {
            newErrors.firstName = 'Ad 2-50 karakter arasında olmalıdır';
        }

        if (formData.lastName && !validationRules.name.pattern.test(formData.lastName)) {
            newErrors.lastName = validationRules.name.message;
        } else if (formData.lastName && (formData.lastName.length < 2 || formData.lastName.length > 50)) {
            newErrors.lastName = 'Soyad 2-50 karakter arasında olmalıdır';
        }

        if (formData.email && !validationRules.email.pattern.test(formData.email)) {
            newErrors.email = validationRules.email.message;
        }

        if (formData.phone && !validationRules.phone.pattern.test(formData.phone)) {
            newErrors.phone = validationRules.phone.message;
        }

        if (formData.position && !validationRules.position.pattern.test(formData.position)) {
            newErrors.position = validationRules.position.message;
        } else if (formData.position && formData.position.length > 100) {
            newErrors.position = 'Pozisyon en fazla 100 karakter olabilir';
        }

        if (!isEdit && formData.password) {
            if (!validationRules.password.pattern.test(formData.password)) {
                newErrors.password = validationRules.password.message;
            }
        } else if (!isEdit && !formData.password) {
            newErrors.password = 'Şifre zorunludur';
        }

        if (formData.emergencyContactName && !validationRules.name.pattern.test(formData.emergencyContactName)) {
            newErrors.emergencyContactName = validationRules.name.message;
        } else if (formData.emergencyContactName && formData.emergencyContactName.length > 100) {
            newErrors.emergencyContactName = 'Acil durum kişi adı en fazla 100 karakter olabilir';
        }

        if (formData.emergencyContactPhone && !validationRules.phone.pattern.test(formData.emergencyContactPhone)) {
            newErrors.emergencyContactPhone = validationRules.phone.message;
        }

        if (!isEdit && (!formData.roleIds || formData.roleIds.length === 0)) {
            newErrors.roleIds = 'En az bir rol seçmelisiniz';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    }, [formData, validationConfig, isEdit]);

    const resetForm = () => {
        setFormData(initialData);
        setErrors({});
    };

    useEffect(() => {
        if (Object.keys(formData).some(key => formData[key] !== initialData[key])) {
            if (Object.keys(errors).length > 0) {
                validateForm();
            }
        }
    }, [formData, errors, initialData, validateForm]);

    return {
        formData,
        setFormData,
        errors,
        handleInputChange,
        handleRoleChange,
        toggleRole,
        validateForm,
        resetForm
    };
};

export default useEmployeeFormValidation;