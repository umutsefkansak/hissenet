// src/components/AuthModal/CodeVerificationModal.jsx
import React, { useState, useRef, useEffect } from 'react';
import './AuthModal.css';

export function CodeVerificationModal({ isOpen, onClose, onConfirm,  maxAttempts, attemptsLeft }) {
  const [digits, setDigits] = useState(['', '', '', '', '', '']);
  const inputs = useRef([]);

  // Modal açıldığında sıfırla ve ilk input’a odaklan
  useEffect(() => {
    if (isOpen) {
      setDigits(['', '', '', '', '', '']);
      inputs.current[0]?.focus();
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const handleChange = (i, val) => {
    if (!/^\d?$/.test(val)) return;
    const next = [...digits];
    next[i] = val;
    setDigits(next);
    if (val && i < 5) inputs.current[i+1]?.focus();
  };

  const handlePaste = (e) => {
    e.preventDefault();
    const pasteData = e.clipboardData.getData('Text').replace(/\D/g, ''); // sadece rakamlar
    if (!pasteData) return;

    const chars = pasteData.split('').slice(0, 6); // max 6 hane
    const newDigits = [...digits];
    for (let i = 0; i < chars.length; i++) {
      newDigits[i] = chars[i];
    }
    setDigits(newDigits);

    // Tüm kutular dolduysa onConfirm'i otomatik çalıştır
    if (chars.length === 6) {
      onConfirm(chars.join(''));
    } else {
      // Son girilen haneden sonrakine odaklan
      inputs.current[chars.length]?.focus();
    }
  };


  const submit = () => {
    const code = digits.join('');
    if (code.length === 6) onConfirm(code);
  };

  return (
    <div className="auth-overlay" onClick={onClose}>
      <div className="auth-modal" onClick={e => e.stopPropagation()}>
        <h2 className="ham-title">E-posta Doğrulama</h2>
        <p>Müşterinin mail adresine gönderilen kodu girin:</p>
         {(maxAttempts != null) && (
           <p style={{ fontSize: '0.9rem', color: '#666', marginTop: 4 }}>
            Kalan hak: <b>{attemptsLeft != null ? attemptsLeft : '-'}/{maxAttempts}</b>
          </p>
        )}
        <div style={{ display:'flex', gap:8, justifyContent:'center', margin:'1rem 0' }}>
          {digits.map((d,i) => (
            <input
              key={i}
              ref={el => inputs.current[i] = el}
              type="text"
              maxLength="1"
              className="auth-input"
              style={{ width:'3rem', textAlign:'center' }}
              value={d}
              onChange={e => handleChange(i, e.target.value)}
              onPaste={handlePaste}
            />
          ))}
        </div>
        <button className="auth-confirm-btn" onClick={submit}>
          Doğrula
        </button>
      </div>
    </div>
  );
}
