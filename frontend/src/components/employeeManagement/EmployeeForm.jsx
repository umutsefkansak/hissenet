import React, { useState, useEffect } from 'react';
import { roleApi } from '../../server/roles';
import useEmployeeFormValidation from '../../hooks/EmployeeManagement/useEmployeeFormValidation';
import { positionOptions } from '../../constants/EmployeeManagement/formOptions';
import styles from './EmployeeForm.module.css';
import Modal from '../Modal/Modal';

const EmployeeForm = ({ employee, onSubmit, onCancel, isEdit = false }) => {
    const [rolesLoading, setRolesLoading] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [availableRoles, setAvailableRoles] = useState([]);
    const [modalConfig, setModalConfig] = useState(null);

    const initialFormData = {
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        position: '',
        password: '',
        emergencyContactName: '',
        emergencyContactPhone: '',
        roleIds: []
    };

    const validationConfig = {
        requiredFields: [
            'firstName', 'lastName', 'email', 'phone', 'position',
            'emergencyContactName', 'emergencyContactPhone'
        ]
    };

    const {
        formData,
        setFormData,
        errors,
        handleInputChange,
        handleRoleChange,
        toggleRole,
        validateForm,
        resetForm
    } = useEmployeeFormValidation(initialFormData, validationConfig, isEdit);

    useEffect(() => {
        const fetchRoles = async () => {
            setRolesLoading(true);
            try {
                const roles = await roleApi.getAllRoles();
                setAvailableRoles(roles);

                if (employee && isEdit && roles.length > 0) {
                    setFormData({
                        id: employee.id,
                        firstName: employee.firstName || '',
                        lastName: employee.lastName || '',
                        email: employee.email || '',
                        phone: employee.phone || '',
                        position: employee.position || '',
                        password: '',
                        emergencyContactName: employee.emergencyContactName || '',
                        emergencyContactPhone: employee.emergencyContactPhone || '',
                        roleIds: employee.roleIds ? [...employee.roleIds] : []
                    });
                }
            } catch (error) {
                console.error('Roller yüklenirken hata oluştu:', error);
            } finally {
                setRolesLoading(false);
            }
        };

        fetchRoles();
    }, [employee, isEdit]);


    const closeModal = () => setModalConfig(null);

    const hasFormChanges = () => {
        if (!employee || !isEdit) return true;

        return (
            formData.firstName !== (employee.firstName || '') ||
            formData.lastName !== (employee.lastName || '') ||
            formData.email !== (employee.email || '') ||
            formData.phone !== (employee.phone || '') ||
            formData.position !== (employee.position || '') ||
            formData.emergencyContactName !== (employee.emergencyContactName || '') ||
            formData.emergencyContactPhone !== (employee.emergencyContactPhone || '') ||
            JSON.stringify(formData.roleIds) !== JSON.stringify(employee.roleIds || []) ||
            (formData.password && formData.password.trim() !== '')
        );
    };

    const handleSubmitClick = (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        const employeeName = isEdit
            ? `${employee?.firstName || ''} ${employee?.lastName || ''}`.trim()
            : `${formData.firstName} ${formData.lastName}`.trim();

        setModalConfig({
            variant: 'confirm',
            title: isEdit ? 'Personel Güncelleme Onayı' : 'Personel Kaydetme Onayı',
            message: isEdit
                ? `${employeeName} adlı personelin bilgilerini güncellemek istediğinizden emin misiniz?`
                : `${employeeName} adlı yeni personeli kaydetmek istediğinizden emin misiniz?`,
            cancelText: 'Vazgeç',
            confirmText: isEdit ? 'Güncelle' : 'Kaydet',
            onConfirm: () => {
                closeModal();
                handleActualSubmit();
            },
            onClose: closeModal,
        });
    };

    const handleCancelClick = () => {
        if (hasFormChanges()) {
            setModalConfig({
                variant: 'warning',
                title: 'Değişiklikleri Kaybet',
                message: 'Yaptığınız değişiklikler kaydedilmeyecek.\n\nDevam etmek istediğinizden emin misiniz?',
                cancelText: 'Kalmaya Devam Et',
                confirmText: 'Evet, Çık',
                onConfirm: () => {
                    closeModal();
                    onCancel();
                },
                onClose: closeModal,
            });
        } else {
            onCancel();
        }
    };

    const handleActualSubmit = async () => {
        setIsLoading(true);

        try {
            const submitData = { ...formData };
            if (isEdit && !submitData.password.trim()) {
                delete submitData.password;
            }

            if (submitData.roleIds !== undefined) {
                if (!submitData.roleIds || submitData.roleIds.length === 0) {
                    submitData.roleIds = [];
                    console.log('Tüm roller kaldırılıyor');
                } else {
                    const uniqueRoleIds = [...new Set(submitData.roleIds.map(id => Number(id)))];
                    submitData.roleIds = uniqueRoleIds;
                    console.log('Roller güncelleniyor:', uniqueRoleIds);
                }
            } else {
                console.log('Role güncellemesi yapılmıyor');
            }

            const result = await onSubmit(submitData);
            return result;

        } catch (error) {
            console.error('Form gönderilirken hata:', error);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    if (rolesLoading) {
        return (
            <div className={styles.employeeFormLoading}>
                <div className={styles.loadingSpinner}></div>
                <p>Form yükleniyor...</p>
            </div>
        );
    }

    return (
        <>
        <form onSubmit={handleSubmitClick} className={styles.employeeForm}>
            <div className={styles.formHeader}>
                <h3>{isEdit ? 'Personel Bilgilerini Düzenle' : 'Yeni Personel Ekle'}</h3>
            </div>

            <div className={styles.formContent}>
                <div className={styles.formRow}>
                    <div className={styles.formGroup}>
                        <label htmlFor="firstName">Ad *</label>
                        <input
                            type="text"
                            id="firstName"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleInputChange}
                            className={errors.firstName ? styles.error : ''}
                            placeholder="Personel adını giriniz"
                        />
                        {errors.firstName && <span className={styles.errorText}>{errors.firstName}</span>}
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="lastName">Soyad *</label>
                        <input
                            type="text"
                            id="lastName"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleInputChange}
                            className={errors.lastName ? styles.error : ''}
                            placeholder="Personel soyadını giriniz"
                        />
                        {errors.lastName && <span className={styles.errorText}>{errors.lastName}</span>}
                    </div>
                </div>

                <div className={styles.formRow}>
                    <div className={styles.formGroup}>
                        <label htmlFor="email">E-posta *</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={formData.email}
                            onChange={handleInputChange}
                            className={errors.email ? styles.error : ''}
                            placeholder="ornek@firma.com"
                        />
                        {errors.email && <span className={styles.errorText}>{errors.email}</span>}
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="phone">Telefon *</label>
                        <div className={`${styles.phoneInputContainer} ${errors.phone ? styles.error : ''}`}>
                            <span className={styles.phonePrefix}>+90</span>
                            <input
                                type="tel"
                                id="phone"
                                name="phone"
                                value={formData.phone}
                                onChange={handleInputChange}
                                className={`${errors.phone ? styles.error : ''} ${styles.phoneInput}`}
                                placeholder="5XX XXX XX XX"
                                maxLength="10"
                            />
                        </div>
                        {errors.phone && <span className={styles.errorText}>{errors.phone}</span>}
                    </div>
                </div>

                <div className={styles.formRow}>
                    <div className={styles.formGroup}>
                        <label htmlFor="position">Pozisyon *</label>
                        <select
                            id="position"
                            name="position"
                            value={formData.position}
                            onChange={handleInputChange}
                            className={errors.position ? styles.error : ''}
                        >
                            <option value="">Pozisyon seçiniz</option>
                            {positionOptions.map(option => (
                                <option key={option.value} value={option.value}>
                                    {option.label}
                                </option>
                            ))}
                        </select>
                        {errors.position && <span className={styles.errorText}>{errors.position}</span>}
                    </div>

                    {!isEdit && (
                        <div className={styles.formGroup}>
                            <label htmlFor="password">Şifre *</label>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                value={formData.password}
                                onChange={handleInputChange}
                                className={errors.password ? styles.error : ''}
                                placeholder="En az 8 karakter, büyük-küçük harf ve rakam içermeli"
                            />
                            {errors.password && <span className={styles.errorText}>{errors.password}</span>}
                        </div>
                    )}
                </div>

                <div className={styles.formRow}>
                    <div className={styles.formGroup}>
                        <label htmlFor="emergencyContactName">Acil Durum Kişisi *</label>
                        <input
                            type="text"
                            id="emergencyContactName"
                            name="emergencyContactName"
                            value={formData.emergencyContactName}
                            onChange={handleInputChange}
                            className={errors.emergencyContactName ? styles.error : ''}
                            placeholder="Acil durumda aranacak kişi"
                        />
                        {errors.emergencyContactName &&
                            <span className={styles.errorText}>{errors.emergencyContactName}</span>}
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="emergencyContactPhone">Acil Durum Telefonu *</label>
                        <div className={`${styles.phoneInputContainer} ${errors.emergencyContactPhone ? styles.error : ''}`}>
                            <span className={styles.phonePrefix}>+90</span>
                            <input
                                type="tel"
                                id="emergencyContactPhone"
                                name="emergencyContactPhone"
                                value={formData.emergencyContactPhone}
                                onChange={handleInputChange}
                                className={`${errors.emergencyContactPhone ? styles.error : ''} ${styles.phoneInput}`}
                                placeholder="5XX XXX XX XX"
                                maxLength="10"
                            />
                        </div>
                        {errors.emergencyContactPhone &&
                            <span className={styles.errorText}>{errors.emergencyContactPhone}</span>}
                    </div>
                </div>
            </div>

            <div className={styles.rolesSection}>
                <h4 className={styles.rolesTitle}>Roller</h4>
                {availableRoles.length > 0 ? (
                    <>
                        {formData.roleIds.length > 0 && (
                            <div className={styles.selectedRolesTags}>
                                {formData.roleIds.map(roleId => {
                                    const role = availableRoles.find(r => r.id === roleId);
                                    return role ? (
                                        <span key={roleId} className={styles.roleTag}>
                                            {role.name}
                                            <button
                                                type="button"
                                                className={styles.roleTagRemove}
                                                onClick={() => toggleRole(roleId)}
                                                title="Rolü kaldır"
                                            >
                                                ×
                                            </button>
                                        </span>
                                    ) : null;
                                })}
                            </div>
                        )}
                        <div className={styles.rolesGrid}>
                            {availableRoles.map(role => (
                                <label
                                    key={role.id}
                                    className={`${styles.roleItem} ${formData.roleIds.includes(role.id) ? styles.selected : ''}`}
                                >
                                    <input
                                        type="checkbox"
                                        value={role.id}
                                        checked={formData.roleIds.includes(role.id)}
                                        onChange={handleRoleChange}
                                    />
                                    <span className={styles.roleName}>{role.name}</span>
                                </label>
                            ))}
                        </div>
                    </>
                ) : (
                    <p className={styles.noRoles}>Rol bulunamadı</p>
                )}
                {errors.roleIds && <span className={styles.errorText}>{errors.roleIds}</span>}
            </div>

            <div className={styles.formActions}>
                <button
                    type="button"
                    onClick={handleCancelClick}
                    className={styles.btnCancel}
                    disabled={isLoading}
                >
                    İptal
                </button>
                <button
                    type="submit"
                    disabled={isLoading}
                    className={styles.btnSubmit}
                >
                    {isLoading ? 'Kaydediliyor...' : (isEdit ? 'Güncelle' : 'Kaydet')}
                </button>
            </div>
        </form>

            {modalConfig && <Modal {...modalConfig} />}
        </>
    );
};

export default EmployeeForm;