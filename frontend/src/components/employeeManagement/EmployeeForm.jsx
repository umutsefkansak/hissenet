import React, { useState, useEffect } from 'react';
import { roleApi } from '../../server/roles';
import useEmployeeFormValidation from '../../hooks/EmployeeManagement/useEmployeeFormValidation';
import './EmployeeForm.css';

const EmployeeForm = ({ employee, onSubmit, onCancel, isEdit = false }) => {

    const [rolesLoading, setRolesLoading] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [availableRoles, setAvailableRoles] = useState([]);

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



    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

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
            <div className="employee-form-loading">
                <div className="loading-spinner"></div>
                <p>Form yükleniyor...</p>
            </div>
        );
    }
    return (
        <form onSubmit={handleSubmit} className="employee-form">
            <div className="form-header">
                <h3>{isEdit ? 'Personel Bilgilerini Düzenle' : 'Yeni Personel Ekle'}</h3>
            </div>

            <div className="form-content">
                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="firstName">Ad *</label>
                        <input
                            type="text"
                            id="firstName"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleInputChange}
                            className={errors.firstName ? 'error' : ''}
                            placeholder="Personel adını giriniz"
                        />
                        {errors.firstName && <span className="error-text">{errors.firstName}</span>}
                    </div>

                    <div className="form-group">
                        <label htmlFor="lastName">Soyad *</label>
                        <input
                            type="text"
                            id="lastName"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleInputChange}
                            className={errors.lastName ? 'error' : ''}
                            placeholder="Personel soyadını giriniz"
                        />
                        {errors.lastName && <span className="error-text">{errors.lastName}</span>}
                    </div>
                </div>



                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="email">E-posta *</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={formData.email}
                            onChange={handleInputChange}
                            className={errors.email ? 'error' : ''}
                            placeholder="ornek@firma.com"
                        />
                        {errors.email && <span className="error-text">{errors.email}</span>}
                    </div>

                    <div className="form-group">
                        <label htmlFor="phone">Telefon *</label>
                        <div className={`phone-input-container ${errors.phone ? 'error' : ''}`}>
                            <span className="phone-prefix">+90</span>
                            <input
                                type="tel"
                                id="phone"
                                name="phone"
                                value={formData.phone}
                                onChange={handleInputChange}
                                className={errors.phone ? 'error phone-input' : 'phone-input'}
                                placeholder="5XX XXX XX XX"
                                maxLength="10"
                            />
                        </div>
                        {errors.phone && <span className="error-text">{errors.phone}</span>}
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="position">Pozisyon *</label>
                        <input
                            type="text"
                            id="position"
                            name="position"
                            value={formData.position}
                            onChange={handleInputChange}
                            className={errors.position ? 'error' : ''}
                            placeholder="Müdür, Uzman, Analyst vb."
                        />
                        {errors.position && <span className="error-text">{errors.position}</span>}
                    </div>

                    {!isEdit && (
                        <div className="form-group">
                            <label htmlFor="password">Şifre *</label>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                value={formData.password}
                                onChange={handleInputChange}
                                className={errors.password ? 'error' : ''}
                                placeholder="En az 8 karakter, büyük-küçük harf ve rakam içermeli"
                            />
                            {errors.password && <span className="error-text">{errors.password}</span>}
                        </div>
                    )}
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="emergencyContactName">Acil Durum Kişisi *</label>
                        <input
                            type="text"
                            id="emergencyContactName"
                            name="emergencyContactName"
                            value={formData.emergencyContactName}
                            onChange={handleInputChange}
                            className={errors.emergencyContactName ? 'error' : ''}
                            placeholder="Acil durumda aranacak kişi"
                        />
                        {errors.emergencyContactName &&
                            <span className="error-text">{errors.emergencyContactName}</span>}
                    </div>

                    <div className="form-group">
                        <label htmlFor="emergencyContactPhone">Acil Durum Telefonu *</label>
                        <div className={`phone-input-container ${errors.emergencyContactPhone ? 'error' : ''}`}>
                            <span className="phone-prefix">+90</span>
                            <input
                                type="tel"
                                id="emergencyContactPhone"
                                name="emergencyContactPhone"
                                value={formData.emergencyContactPhone}
                                onChange={handleInputChange}
                                className={errors.emergencyContactPhone ? 'error phone-input' : 'phone-input'}
                                placeholder="5XX XXX XX XX"
                                maxLength="10"
                            />
                        </div>
                        {errors.emergencyContactPhone &&
                            <span className="error-text">{errors.emergencyContactPhone}</span>}
                    </div>
                </div>
            </div>
            <div className="roles-section">
                <h4 className="roles-title">Roller</h4>
                {availableRoles.length > 0 ? (
                    <>
                        {formData.roleIds.length > 0 && (
                            <div className="selected-roles-tags">
                                {formData.roleIds.map(roleId => {
                                    const role = availableRoles.find(r => r.id === roleId);
                                    return role ? (
                                        <span key={roleId} className="role-tag">
                                            {role.name}
                                            <button
                                                type="button"
                                                className="role-tag-remove"
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
                        <div className="roles-grid">
                            {availableRoles.map(role => (
                                <label
                                    key={role.id}
                                    className={`role-item ${formData.roleIds.includes(role.id) ? 'selected' : ''}`}
                                >
                                    <input
                                        type="checkbox"
                                        value={role.id}
                                        checked={formData.roleIds.includes(role.id)}
                                        onChange={handleRoleChange}
                                    />
                                    <span className="role-name">{role.name}</span>
                                </label>
                            ))}
                        </div>
                    </>
                ) : (
                    <p className="no-roles">Rol bulunamadı</p>
                )}
                {errors.roleIds && <span className="error-text">{errors.roleIds}</span>}
            </div>

            <div className="form-actions">
                <button
                    type="button"
                    onClick={onCancel}
                    className="btn-cancel"
                    disabled={isLoading}
                >
                    İptal
                </button>
                <button
                    type="submit"
                    disabled={isLoading}
                    className="btn-submit"
                >
                    {isLoading ? 'Kaydediliyor...' : (isEdit ? 'Güncelle' : 'Kaydet')}
                </button>
            </div>
        </form>
    );
};

export default EmployeeForm;