import React from 'react';
import styles from '../../pages/CreateCustomer/CreateCustomer.module.css';

const FormSelect = ({
                        label,
                        name,
                        value,
                        onChange,
                        error,
                        required = false,
                        options = [],
                        placeholder = "Seçiniz...",
                        ...props
                    }) => {
    return (
        <div className={styles.formComponentGroup}>
            <label htmlFor={name}>
                {label} {required && <span className={styles.required}>*</span>}
            </label>
            <select
                id={name}
                name={name}
                value={value}
                onChange={onChange}
                className={error ? styles.error : ''}
                {...props}
            >
                <option value="">{placeholder}</option>
                {options.map((option) => (
                    <option key={option.value} value={option.value}>
                        {option.label}
                    </option>
                ))}
            </select>
            {error && <span className={styles.errorText}>{error}</span>}
        </div>
    );
};

export default FormSelect;
