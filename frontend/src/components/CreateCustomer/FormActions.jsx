import React from 'react';
import styles from '../../pages/CreateCustomer/CreateCustomer.module.css';

const FormActions = ({ onGoBack, onSubmit, isLoading }) => {
    return (
        <div className={styles.formActions}>
            <button
                type="submit"
                disabled={isLoading}
                className={styles.createCustomerBtnPrimary}
            >
                {isLoading ? 'Kaydediliyor...' : 'Kaydet'}
            </button>
        </div>
    );
};

export default FormActions;