import React from 'react';
import './EmployeeStatusBadge.css';

const EmployeeStatusBadge = ({ status }) => {
    const getStatusClass = (status) => {
        switch (status?.toLowerCase()) {
            case 'ACTIVE':
            case 'aktif':
                return 'status-active';
            case 'INACTIVE':
            case 'pasif':
                return 'status-inactive';
            default:
                return 'status-active';
        }
    };

    const getStatusText = (status) => {
        switch (status?.toLowerCase()) {
            case 'active':
            case 'aktif':
                return 'Aktif';
            case 'inactive':
            case 'pasif':
                return 'Pasif';
            default:
                return 'Aktif';
        }
    };

    return (
        <span className={`status-badge ${getStatusClass(status)}`}>
            {getStatusText(status)}
        </span>
    );
};

export default EmployeeStatusBadge;