import React from 'react';
import './DashboardCard.css';

const DashboardCard = ({
                           title,
                           icon,
                           value,
                           subtitle,
                           iconVariant = 'default',
                           className = ''
                       }) => {
    return (
        <div className={`dashboard-card ${className}`}>
            <div className="card-header">
                <h3>{title}</h3>
                <div className={`card-icon ${iconVariant}`}>
                    {icon}
                </div>
            </div>
            <div className="card-value">{value}</div>
            <div className="card-subtitle">{subtitle}</div>
        </div>
    );
};

export default DashboardCard;
