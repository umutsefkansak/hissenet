import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { sendPasswordResetCode } from '../../server/mail';
import './ForgotPassword.css';

const ForgotPassword = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (!email || !email.includes('@')) {
      setError('Geçerli bir e-posta adresi giriniz.');
      setLoading(false);
      return;
    }

    try {
      const result = await sendPasswordResetCode(email);
      
      if (result.success) {
        window.showToast('Şifre sıfırlama kodu gönderildi!', 'success', 2000);
        navigate('/verification-code', { state: { email } });
      } else {
        // API error response'u string'e çevir
        const errorMessage = typeof result.error === 'object' 
          ? result.error.message || result.error.detail || 'Şifre sıfırlama kodu gönderilemedi.'
          : result.error || 'Şifre sıfırlama kodu gönderilemedi.';
        setError(errorMessage);
      }
    } catch (error) {
      setError('Bir hata oluştu. Lütfen tekrar deneyin.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="forgot-password-container">
      <div className="forgot-password-card">
        <div className="forgot-password-header">
          <div className="email-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
              <polyline points="22,6 12,13 2,6"/>
            </svg>
          </div>
          <h1 className="forgot-password-title">Şifremi Unuttum</h1>
          <p className="forgot-password-description">
            E-posta adresinizi girin, size doğrulama kodu gönderelim.
          </p>
        </div>

        <form className="forgot-password-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">E-posta Adresi</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="ornek@email.com"
              className="form-input"
              required
            />
          </div>

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <button type="submit" className="submit-button" disabled={loading}>
            {loading ? 'Gönderiliyor...' : 'Doğrulama Kodu Gönder'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default ForgotPassword; 