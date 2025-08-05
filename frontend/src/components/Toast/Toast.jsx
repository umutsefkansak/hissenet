import React, { useState, useEffect } from 'react';
import './Toast.css';

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

  return (
    <div className={`toast ${type} ${isVisible ? 'show' : 'hide'}`}>
      <div className="toast-content">
        <span className="toast-message">{message}</span>
        <button className="toast-close" onClick={() => {
          setIsVisible(false);
          setTimeout(() => onClose(), 300);
        }}>
          ×
        </button>
      </div>
    </div>
  );
};

export default Toast; 