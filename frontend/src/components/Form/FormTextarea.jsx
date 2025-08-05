import React from 'react';

const FormTextarea = ({
                          label,
                          name,
                          value,
                          onChange,
                          error,
                          required = false,
                          placeholder,
                          rows = 3,
                          ...props
                      }) => {
    return (
        <div className="form-group">
            <label htmlFor={name}>
                {label} {required && <span className="required">*</span>}
            </label>
            <textarea
                id={name}
                name={name}
                value={value}
                onChange={onChange}
                className={error ? 'error' : ''}
                placeholder={placeholder}
                rows={rows}
                {...props}
            />

            {error && <span className="error-message">{error}</span>}
        </div>
    );
};

export default FormTextarea;