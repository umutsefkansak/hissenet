import React, { useState } from 'react';
import { sendPasswordChangeToken } from '../../server/mail';
import './ForgotPassword.css';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

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
      const result = await sendPasswordChangeToken(email);
      
      if (result.success) {
        setSuccess(true);
        setEmail(''); // Form'u temizle
        setTimeout(() => {
          setSuccess(false);
        }, 3000);
      } else {
        // API error response'u string'e çevir
        const errorMessage = typeof result.error === 'object' 
          ? result.error.message || result.error.detail || 'Şifre değiştirme linki gönderilemedi.'
          : result.error || 'Şifre değiştirme linki gönderilemedi.';
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
      <div className={`forgot-password-card ${success ? 'success' : ''}`}>
        <div className="forgot-password-header">
          <div className="email-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
              <polyline points="22,6 12,13 2,6"/>
            </svg>
          </div>
          <h1 className="forgot-password-title">Şifremi Unuttum</h1>
          <p className="forgot-password-description">
            E-posta adresinizi girin, size şifre değiştirme linki gönderelim.
          </p>
        </div>

        {success ? (
          <div className="success-message">
            <div className="success-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                <polyline points="22,4 12,14.01 9,11.01"/>
              </svg>
            </div>
            <h2>E-posta Gönderildi!</h2>
            <p>Şifre değiştirme linki e-posta adresinize gönderildi. Lütfen e-postanızı kontrol edin.</p>
          </div>
        ) : (
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
              {loading ? 'Gönderiliyor...' : 'Şifre Değiştirme Linki Gönder'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
};

export default ForgotPassword; 