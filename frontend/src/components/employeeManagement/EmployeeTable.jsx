import EmployeeStatusBadge from './EmployeeStatusBadge';
import './EmployeeTable.css';
import React, { useState, useEffect } from 'react';
import { roleApi } from '../../server/roles';
import Modal from '../Modal/Modal';

const EmployeeTable = ({ employees, onEdit, onDelete, loading }) => {
    const [roleMap, setRoleMap] = useState(new Map());
    const [modalConfig, setModalConfig] = useState(null);

    useEffect(() => {
        const fetchRoles = async () => {
            try {
                const roles = await roleApi.getAllRoles();
                const map = new Map();
                roles.forEach(role => {
                    map.set(role.id, role.name);
                });
                setRoleMap(map);
            } catch (error) {
                console.error('Roller yüklenirken hata:', error);
            }
        };

        fetchRoles();
    }, []);

    const getRoleName = (roleId) => {
        return roleMap.get(roleId) || `Role ${roleId}`;
    };
    const getInitials = (firstName, lastName) => {
        return `${firstName?.charAt(0) || ''}${lastName?.charAt(0) || ''}`.toUpperCase();
    };

    const formatDate = (dateString) => {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('tr-TR');
    };

    const closeModal = () => setModalConfig(null);
    const handleDeleteClick = (employee) => {
        const employeeName = `${employee.firstName || ''} ${employee.lastName || ''}`.trim() || 'Bu personel';

        setModalConfig({
            variant: 'warning',
            title: 'Personel Silme Onayı',
            message: `${employeeName} adlı personeli silmek istediğinizden emin misiniz?\n\nBu işlem geri alınamaz.`,
            cancelText: 'Vazgeç',
            confirmText: 'Sil',
            onConfirm: () => {
                closeModal();
                onDelete(employee);
            },
            onClose: closeModal,
        });
    };

    if (loading) {
        return (
            <div className="employee-table-container">
                <div className="table-loading">
                    <div className="loading-spinner"></div>
                    <p>Personeller yükleniyor...</p>
                </div>
            </div>
        );
    }

    if (!employees || employees.length === 0) {
        return (
            <div className="employee-table-container">
                <div className="empty-state">
                    <div className="empty-icon">
                        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="#9CA3AF" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <circle cx="12" cy="7" r="4" stroke="#9CA3AF" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                    </div>
                    <h3>Henüz personel bulunmuyor</h3>
                    <p>İlk personeli eklemek için "Personel Ekle" butonuna tıklayın.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="employee-table-container">
            <div className="table-wrapper">
                <table className="employee-table">
                    <thead>
                    <tr>
                        <th>Ad Soyad</th>
                        <th>Email</th>
                        <th>Telefon</th>
                        <th>Roller</th>
                        <th>Durum</th>
                        <th>İşlemler</th>
                    </tr>
                    </thead>
                    <tbody>
                    {employees.map((employee) => (
                        <tr key={employee.id}>
                            <td>
                                <div className="employee-name">
                                    <div className="employee-avatar">
                                        {getInitials(employee.firstName, employee.lastName)}
                                    </div>
                                    <div className="employee-info">
                                        <span
                                            className="name">{`${employee.firstName || ''} ${employee.lastName || ''}`.trim() || 'N/A'}</span>
                                        <span
                                            className="position">{employee.position || 'Pozisyon belirtilmemiş'}</span>
                                    </div>
                                </div>
                            </td>
                            <td>{employee.email || 'N/A'}</td>
                            <td>{employee.phone || 'N/A'}</td>
                            <td>
                                <div className="roles-badges">
                                    {employee.roleIds && employee.roleIds.length > 0 ? (
                                        Array.from(employee.roleIds).map((roleId, index) => (
                                            <span key={roleId || index} className="role-badge">
                                                    {getRoleName(roleId)}
                                                </span>
                                        ))
                                    ) : (
                                        <span className="role-badge role-badge-empty">Rol atanmamış</span>
                                    )}
                                </div>
                            </td>
                            <td>
                                <EmployeeStatusBadge status={employee.status}/>
                            </td>
                            <td>
                                <div className="action-buttons">
                                    <button
                                        className="edit-button"
                                        onClick={() => onEdit(employee)}
                                    >
                                        Düzenle
                                    </button>
                                    <button
                                        className="delete-button"
                                        onClick={() => handleDeleteClick(employee)}
                                    >
                                        Sil
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
            {modalConfig && <Modal {...modalConfig} />}
        </div>
    );
};

export default EmployeeTable;