import { useState, useEffect, useRef, useCallback } from 'react';
import { createPortal } from 'react-dom';
import './AuthModal.css';
import logo from '../../images/logo-transparan1.png'; // replace with your logo path

export default function AuthModal({ isOpen, onClose, onConfirm, isProcessing = false }) {
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

  const onlyDigits = useCallback((s) => s.replace(/\D/g, ''), []);
  const isValid = value.length === 10 || value.length === 11; // VKN 10, TCKN 11
  const disabled = isProcessing || !isValid;

  const handleChange = (e) => {
    const digits = onlyDigits(e.target.value);
    setValue(digits.slice(0, 11));
  };

  const handleConfirm = () => {
    if (disabled) return;
    onConfirm?.(value);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleConfirm();
    if (e.key === 'Escape' && !isProcessing) onClose?.();
  };


  if (!isOpen) return null;

  const node = (
    <div
      className="auth-overlay"
      onClick={() => { if (!isProcessing) onClose?.(); }}
      onKeyDown={handleKeyDown}
      role="dialog"
      aria-modal="true"
    >
      <div className="auth-modal" onClick={e => e.stopPropagation()}>
        <img src={logo} alt="Hissenet Logo" className="auth-logo" />
        <h2 className="ham-title">T.C. Kimlik No/VKN</h2>
        <input
          ref={inputRef}
          type="text"
          className="auth-input"
          placeholder="İşlem yapılacak müşterinin T.C. Kimlik No / VKN'sini giriniz."
          value={value}
          onChange={handleChange}
          inputMode="numeric"
          autoComplete="off"
          aria-invalid={!isValid && value.length > 0}
        />
        <button
          className={`auth-confirm-btn ${disabled ? 'is-disabled' : ''} ${isProcessing ? 'is-loading' : ''}`}
          onClick={handleConfirm}
          disabled={disabled}
        >
          {isProcessing ? <span className="spinner" aria-hidden="true" /> : 'Onayla'}
        </button>
      </div>
    </div>
  );
  return createPortal(node, document.body);

}
