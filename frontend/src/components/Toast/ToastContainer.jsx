import React, { useState } from 'react';
import Toast from './Toast';
import styles from './Toast.module.css';

const ToastContainer = () => {
  const [toasts, setToasts] = useState([]);

  // Global function to show toast
  const showToast = (message, type = 'info', duration = 3000) => {
    const id = Date.now() + Math.random(); // Daha unique ID
    const newToast = { id, message, type, duration };
    setToasts(prev => [...prev, newToast]);
  };

  // Remove toast
  const removeToast = (id) => {
    setToasts(prev => prev.filter(toast => toast.id !== id));
  };

  // Make showToast globally available
  React.useEffect(() => {
    window.showToast = showToast;
    return () => {
      delete window.showToast;
    };
  }, []);

  return (
      <div className={styles.toastContainer}>
        {toasts.map((toast, index) => (
            <Toast
                key={toast.id}
                message={toast.message}
                type={toast.type}
                duration={toast.duration}
                onClose={() => removeToast(toast.id)}
                style={{
                  transform: `translateY(${index * 80}px)`
                }}
            />
        ))}
      </div>
  );
};

export default ToastContainer;