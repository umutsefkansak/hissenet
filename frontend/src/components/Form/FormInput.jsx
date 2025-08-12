import React from 'react';
import styles from '../../pages/CreateCustomer/CreateCustomer.module.css';

const FormInput = ({
                       label,
                       name,
                       type = 'text',
                       value,
                       onChange,
                       error,
                       required = false,
                       placeholder,
                       maxLength,
                       ...props
                   }) => {
    return (
        <div className={styles.formComponentGroup}>
            <label htmlFor={name}>
                {label} {required && <span className={styles.required}>*</span>}
            </label>
            <input
                type={type}
                id={name}
                name={name}
                value={value}
                onChange={onChange}
                className={error ? styles.error : ''}
                placeholder={placeholder}
                maxLength={maxLength}
                {...props}
            />
            {error && <span className={styles.errorText}>{error}</span>}
        </div>
    );
};

export default FormInput;