import React from 'react';

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
        <div className="form-group">
            <label htmlFor={name}>
                {label} {required && <span className="required">*</span>}
            </label>
            <input
                type={type}
                id={name}
                name={name}
                value={value}
                onChange={onChange}
                className={error ? 'error' : ''}
                placeholder={placeholder}
                maxLength={maxLength}
                {...props}
            />

            {error && <span className="error-message">{error}</span>}
        </div>

    );
};

export default FormInput;