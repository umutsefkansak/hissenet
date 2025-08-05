import React from 'react';

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
        <div className="form-group">
            <label htmlFor={name}>
                {label} {required && <span className="required">*</span>}
            </label>
            <select
                id={name}
                name={name}
                value={value}
                onChange={onChange}
                className={error ? 'error' : ''}
                {...props}
            >
                <option value="">{placeholder}</option>
                {options.map((option) => (
                    <option key={option.value} value={option.value}>
                        {option.label}
                    </option>
                ))}
            </select>

            {error && <span className="error-message">{error}</span>}
        </div>
    );
};

export default FormSelect;