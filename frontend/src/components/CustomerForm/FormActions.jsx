import React from 'react';

const FormActions = ({ onGoBack, onSubmit, isLoading }) => {
    return (
        <div className="form-actions">
            <button
                type="button"
                className="btn-secondary"
                onClick={onGoBack}
            >
                Geri Dön
            </button>
            <button
                type="submit"
                className="btn-primary"
                disabled={isLoading}
                onClick={onSubmit}
            >
                {isLoading ? 'Kaydediliyor...' : 'Kaydet'}
            </button>
        </div>
    );
};

export default FormActions;