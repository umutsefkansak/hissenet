import React from 'react';

const FormActions = ({ onGoBack, onSubmit, isLoading }) => {
    return (
        <div className="form-actions">
            <button
                type="button"
                className="create-customer-btn-secondary"
                onClick={onGoBack}
            >
                Geri Dön
            </button>
            <button
                type="submit"
                className="create-customer-btn-primary"
                disabled={isLoading}
                onClick={onSubmit}
            >
                {isLoading ? 'Kaydediliyor...' : 'Kaydet'}
            </button>
        </div>
    );
};

export default FormActions;