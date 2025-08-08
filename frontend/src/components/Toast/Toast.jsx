import React, { useState, useEffect } from 'react';
import styles from './Toast.module.css';

const Toast = ({ message, type = 'info', duration = 3000, onClose }) => {
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsVisible(false);
      setTimeout(() => {
        onClose();
      }, 300); // CSS transition duration
    }, duration);

    return () => clearTimeout(timer);
  }, [duration, onClose]);

  const handleClose = () => {
    setIsVisible(false);
    setTimeout(() => onClose(), 300);
  };

  return (
      <div className={`${styles.toast} ${styles[type]} ${isVisible ? styles.show : styles.hide}`}>
        <div className={styles.toastContent}>
          <span className={styles.toastMessage}>{message}</span>
          <button className={styles.toastClose} onClick={handleClose}>
            ×
          </button>
        </div>
      </div>
  );
};

export default Toast;