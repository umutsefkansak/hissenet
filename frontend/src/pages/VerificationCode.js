import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './VerificationCode.css';

const VerificationCode = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showWarning, setShowWarning] = useState(false);
  const [attempts, setAttempts] = useState(0);

  const email = location.state?.email || 'akkoksinan@gmail.com';

  useEffect(() => {
    if (attempts > 0) {
      setShowWarning(true);
    }
  }, [attempts]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // Simüle edilmiş API çağrısı
    setTimeout(() => {
      if (code === '123456') {
        // Başarılı - yeni şifre sayfasına yönlendir
        window.showToast('Kod doğrulandı!', 'success', 2000);
        navigate('/new-password', { state: { email } });
      } else {
        setAttempts(attempts + 1);
        setError('Doğrulama kodunu yanlış girdiniz');
      }
      setLoading(false);
    }, 1500);
  };

  const handleResendCode = () => {
    setLoading(true);
    setTimeout(() => {
      window.showToast('Yeni kod gönderildi!', 'success', 2000);
      setLoading(false);
    }, 1000);
  };

  return (
    <div className="verification-container">
      <div className="verification-card">
        {showWarning && (
          <div className="warning-banner">
            <span className="warning-icon">⚠️</span>
            <span className="warning-text">Doğrulama kodunu yanlış girdiniz</span>
          </div>
        )}

        <div className="verification-header">
          <h1 className="verification-title">Doğrulama Kodu</h1>
          <p className="verification-description">
            {email} adresinden kodu girin
          </p>
        </div>

        <form className="verification-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <input
              type="text"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              placeholder="123456"
              className="code-input"
              maxLength="6"
              required
            />
          </div>

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <button type="submit" className="verify-button" disabled={loading}>
            {loading ? 'Doğrulanıyor...' : 'Kodu Doğrula'}
          </button>

          <button 
            type="button" 
            className="resend-button" 
            onClick={handleResendCode}
            disabled={loading}
          >
            Kodu tekrar gönder
          </button>
        </form>
      </div>
    </div>
  );
};

export default VerificationCode; 