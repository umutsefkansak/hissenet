import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { verifyCode, sendVerificationCode } from '../../server/api';
import './VerificationCode.css';

const VerificationCode = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showWarning, setShowWarning] = useState(false);
  const [attempts, setAttempts] = useState(0);

  const email = location.state?.email || 'ornek@gmail.com';

  useEffect(() => {
    if (attempts > 0) {
      setShowWarning(true);
    }
  }, [attempts]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (!code || code.length !== 6) {
      setError('6 haneli doğrulama kodunu giriniz.');
      setLoading(false);
      return;
    }

    try {
      const result = await verifyCode(email, code);
      
      if (result.success && result.data?.data?.success) {
        // Kod doğrulandı - yeni şifre sayfasına yönlendir
        window.showToast('Kod doğrulandı!', 'success', 2000);
        navigate('/new-password', { state: { email } });
      } else {
        setAttempts(attempts + 1);
        const remainingAttempts = result.data?.data?.remainingAttempts || 0;
        const isBlocked = result.data?.data?.blocked || false;
        
        if (isBlocked) {
          setError('Çok fazla hatalı deneme yaptınız. Lütfen daha sonra tekrar deneyin.');
        } else if (remainingAttempts === 0) {
          setError('3 hatalı deneme yaptınız. Şifre sıfırlama işlemi engellendi.');
        } else {
          setError(`Doğrulama kodunu yanlış girdiniz. Kalan deneme: ${remainingAttempts}`);
        }
      }
    } catch (error) {
      setError('Bir hata oluştu. Lütfen tekrar deneyin.');
    } finally {
      setLoading(false);
    }
  };

  const handleResendCode = async () => {
    setLoading(true);
    setError('');
    
    try {
      const result = await sendVerificationCode(email);
      
      if (result.success) {
        window.showToast('Yeni kod gönderildi!', 'success', 2000);
        setAttempts(0); // Deneme sayısını sıfırla
             } else {
         // API error response'u string'e çevir
         const errorMessage = typeof result.error === 'object' 
           ? result.error.message || result.error.detail || 'Kod gönderilemedi.'
           : result.error || 'Kod gönderilemedi.';
         setError(errorMessage);
       }
    } catch (error) {
      setError('Bir hata oluştu. Lütfen tekrar deneyin.');
    } finally {
      setLoading(false);
    }
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

          <button 
            type="submit" 
            className="verify-button" 
            disabled={loading || attempts >= 3}
          >
            {loading ? 'Doğrulanıyor...' : attempts >= 3 ? 'Engellendi' : 'Kodu Doğrula'}
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