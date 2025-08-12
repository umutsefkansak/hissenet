import React from 'react';
import PropTypes from 'prop-types';
import { MODAL_VARIANTS } from './variantConfig';
import Button from './button/Button';
import styles from './Modal.module.css';

export default function Modal({
 variant = 'success',
  title,
  message,
  onConfirm,
  onClose,
  confirmText = 'Tamam',
  cancelText = 'İptal',
}) {

  const { Icon, color } = MODAL_VARIANTS[variant] || MODAL_VARIANTS.success;
  const isTwoButtons = ['warning', 'confirm'].includes(variant);
  
  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.dialog} onClick={e => e.stopPropagation()}>
        <div className={styles.header}>
          <div
            className={styles.iconWrapper}
            style={{ backgroundColor: color }}
          >
            <Icon size={48} color="#fff" />
          </div>
          <h2 className={styles.title}>{title}</h2>
        </div>
        <div className={styles.body}>
          {typeof message === 'string'
            ? message.split('\n').map((line, i) => <p key={i}>{line}</p>)
            : message}
        </div>
        <div
          className={`${styles.footer} ${
            isTwoButtons ? styles.multiple : styles.single
          }`}
        >
          {isTwoButtons ? (
            <>
              <Button
                label={cancelText}
                onClick={onClose}
                variant="default"
              />
              <Button
                label={confirmText}
                onClick={onConfirm}
                variant="primary"
              />
            </>
          ) : (
            <Button
              label={confirmText}
              onClick={onClose}
              variant={variant === 'error' ? 'danger' : 'primary'}
            />
          )}
        </div>
      </div>
    </div>
  );
}

Modal.propTypes = {
  variant: PropTypes.oneOf(Object.keys(MODAL_VARIANTS)),
  title: PropTypes.string.isRequired,
  message: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.node
  ]).isRequired,
  onConfirm: PropTypes.func,
  onClose: PropTypes.func.isRequired,
  confirmText: PropTypes.string,
  cancelText: PropTypes.string,
};
