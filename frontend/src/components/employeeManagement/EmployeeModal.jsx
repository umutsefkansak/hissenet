import React, { useEffect } from 'react';
import './EmployeeModal.css';

const EmployeeModal = ({ isOpen, onClose, children }) => {
    useEffect(() => {
        const handleKeyDown = (event) => {
            if (event.key === 'Escape') {
                onClose();
            }
        };

        if (isOpen) {
            document.addEventListener('keydown', handleKeyDown);
            document.body.style.overflow = 'hidden';
        }

        return () => {
            document.removeEventListener('keydown', handleKeyDown);
            document.body.style.overflow = 'unset';
        };
    }, [isOpen, onClose]);

    if (!isOpen) return null;

    const handleOverlayClick = (e) => {
        if (e.target === e.currentTarget) {
            onClose();
        }
    };

    return (
        <div className="hissenet-modal-overlay" onClick={handleOverlayClick}>
            <div className="hissenet-modal-container">
                <button
                    className="hissenet-modal-close-btn"
                    onClick={onClose}
                    aria-label="Kapat"
                >
                    ×
                </button>
                <div className="hissenet-modal-content">
                    {React.isValidElement(children) ? children : <div>{children}</div>}
                </div>
            </div>
        </div>
    );
};

export default EmployeeModal;