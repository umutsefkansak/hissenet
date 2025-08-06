// Button.jsx
import React from 'react';
import styles from './Button.module.css';
import PropTypes from 'prop-types';

export default function Button({
  label,
  onClick,
  variant = 'default',
  type = 'button',
}) {
  return (
    <button
      type={type}
      className={`${styles.button} ${styles[variant]}`}
      onClick={onClick}
    >
      {label}
    </button>
  );
}

Button.propTypes = {
  label: PropTypes.string.isRequired,
  onClick: PropTypes.func.isRequired,
  variant: PropTypes.oneOf(['default', 'primary', 'danger']),
  type: PropTypes.oneOf(['button', 'submit']),
};
