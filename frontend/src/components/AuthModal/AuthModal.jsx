import { useState, useEffect, useRef } from 'react';
import { createPortal } from 'react-dom';
import './AuthModal.css';
import logo from '../../images/logo-transparan1.png'; // replace with your logo path

export default function AuthModal({ isOpen, onClose, onConfirm }) {
  const [value, setValue] = useState('');
   const inputRef = useRef(null);

  useEffect(() => {
    if (!isOpen) return;
    setValue('');
    setTimeout(() => inputRef.current?.focus(), 0);

    // body scroll kilidi
    const prev = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    return () => { document.body.style.overflow = prev; };
  }, [isOpen]);

  if (!isOpen) return null;

  const node = (
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
    return createPortal(node, document.body);

}
