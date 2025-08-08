import { useState, useEffect, useRef } from 'react';
import './AuthModal.css';
import logo from '../../images/logo-transparan1.png'; // replace with your logo path

export default function AuthModal({ isOpen, onClose, onConfirm }) {
  const [value, setValue] = useState('');
   const inputRef = useRef(null);

  useEffect(() => {
    if (isOpen) {
      setValue('');
      setTimeout(() => inputRef.current?.focus(), 0);
    }
  }, [isOpen]);

  if (!isOpen) return null;

  return (
     <div className="auth-overlay" onClick={onClose}>
      <div className="auth-modal" onClick={e => e.stopPropagation()}>
        <img src={logo} alt="Hissenet Logo" className="auth-logo" />
        <h2 className="ham-title">T.C. Kimlik No/VKN</h2>
        <input
          type="text"
          className="auth-input"
          placeholder="İşlem yapılacak müşterinin T.C. Kimlik No / VKN'sini giriniz."
          value={value}
          onChange={e => setValue(e.target.value)}
        />
        <button 
        className="auth-confirm-btn" 
        onClick={() => {
          onConfirm(value);
        }}>
          Onayla
        </button>
      </div>
    </div>
  );
}
