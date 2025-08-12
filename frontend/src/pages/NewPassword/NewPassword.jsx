import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { changePassword } from '../../server/employees';
import { verifyPasswordChangeToken } from '../../server/mail';
import './NewPassword.css';

const NewPassword = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [email, setEmail] = useState('');
  const [tokenValid, setTokenValid] = useState(false);
  const [tokenLoading, setTokenLoading] = useState(true);

  // Token'ı URL'den al ve doğrula
  useEffect(() => {
    const token = searchParams.get('token');
    
    if (!token || token.trim() === '') {
      setError('Bağlantının süresi dolmuş. Lütfen yeni bir bağlantı isteyin.');
      setTokenLoading(false);
      return;
    }

    const verifyToken = async () => {
      try {
        const result = await verifyPasswordChangeToken(token);
        
        if (result.success && result.data?.data?.valid) {
          setEmail(result.data.data.email);
          setTokenValid(true);
        } else {
          setError('Token hatalı. Lütfen daha sonra tekrar deneyin.');
        }
      } catch (error) {
        setError('Token doğrulanırken hata oluştu. Lütfen daha sonra tekrar deneyin.');
      } finally {
        setTokenLoading(false);
      }
    };

    verifyToken();
  }, [searchParams]);

  // Token yükleniyorsa loading göster
  if (tokenLoading) {
    return (
      <div className="new-password-container">
        <div className="new-password-card">
          <div className="new-password-header">
            <h1 className="new-password-title">Yükleniyor...</h1>
            <p className="new-password-description">
              Token doğrulanıyor, lütfen bekleyin.
            </p>
          </div>
        </div>
      </div>
    );
  }

  // Token geçersizse hata mesajı göster
  if (!tokenValid) {
    return (
      <div className="new-password-container">
        <div className="new-password-card">
          <div className="new-password-header">
            <h1 className="new-password-title">Geçersiz Link</h1>
            <p className="new-password-description">
              {error}
            </p>
            <button 
              className="submit-button" 
              onClick={() => navigate('/forgot-password')}
              style={{ marginTop: '20px' }}
            >
              Şifre Sıfırlama Sayfasına Dön
            </button>
          </div>
        </div>
      </div>
    );
  }

  const validatePassword = (password) => {
    const requirements = {
      length: password.length >= 8,
      uppercase: /[A-Z]/.test(password),
      digit: /\d/.test(password),
      special: /[!@#$%^&*]/.test(password)
    };
    return requirements;
  };

  const getPasswordStrength = (password) => {
    if (password.length === 0) return 0;
    
    let strength = 0;
    if (password.length >= 8) strength += 1;
    if (/[A-Z]/.test(password)) strength += 1;
    if (/[a-z]/.test(password)) strength += 1;
    if (/\d/.test(password)) strength += 1;
    if (/[!@#$%^&*]/.test(password)) strength += 1;
    
    return Math.min(strength, 4);
  };

  const requirements = validatePassword(password);
  const passwordStrength = getPasswordStrength(password);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // Şifre gereksinimlerini kontrol et
    if (!requirements.length || !requirements.uppercase || !requirements.digit || !requirements.special) {
      setError('Lütfen tüm şifre gereksinimlerini karşılayın.');
      setLoading(false);
      return;
    }

    // Şifreleri karşılaştır
    if (password !== confirmPassword) {
      setError('Şifreler eşleşmiyor.');
      setLoading(false);
      return;
    }

    try {
      const result = await changePassword(email, password, confirmPassword);
      
      if (result.success) {
        window.showToast('Şifre başarıyla güncellendi!', 'success', 2000);
        setTimeout(() => {
          navigate('/login');
        }, 1000);
      } else {
        setError(result.error || 'Şifre güncellenirken bir hata oluştu.');
      }
    } catch (error) {
      setError('Bir hata oluştu. Lütfen tekrar deneyin.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="new-password-container">
      <div className="new-password-card">
        <div className="new-password-header">
          <h1 className="new-password-title">Yeni Şifre</h1>
          <p className="new-password-description">
            Yeni şifrenizi oluşturun. ({email})
          </p>
        </div>

        <form className="new-password-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">YENİ ŞİFRE</label>
            <div className="password-input-container">
              <input
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Yeni şifrenizi girin"
                className="password-input"
                required
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword(!showPassword)}
              >
                <svg className="eye-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  {showPassword ? (
                      <>
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                        <circle cx="12" cy="12" r="3"/>
                      </>
                  ) : (
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                  )}
                </svg>
              </button>
            </div>
            {/* Password Strength Indicator */}
            {password.length > 0 && (
              <div className="password-strength">
                {[1, 2, 3, 4].map((level) => (
                  <div
                    key={level}
                    className={`strength-bar ${
                      passwordStrength >= level
                        ? passwordStrength <= 1
                          ? 'weak'
                          : passwordStrength <= 2
                          ? 'medium'
                          : passwordStrength <= 3
                          ? 'strong'
                          : 'very-strong'
                        : ''
                    }`}
                  />
                ))}
              </div>
            )}
          </div>

          <div className="form-group">
            <label className="form-label">ŞİFREYİ ONAYLA</label>
            <div className="password-input-container">
              <input
                type={showConfirmPassword ? "text" : "password"}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Şifrenizi tekrar girin"
                className="password-input"
                required
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              >
                <svg className="eye-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  {showConfirmPassword ? (
                      <>
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                        <circle cx="12" cy="12" r="3"/>
                      </>
                  ) : (
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                  )}
                </svg>
              </button>
            </div>
          </div>

          <div className="password-requirements">
            <h3 className="requirements-title">ŞİFRE GEREKSİNİMLERİ:</h3>
            <ul className="requirements-list">
              <li className={requirements.length ? "requirement met" : "requirement"}>
                En az 8 karakter
              </li>
              <li className={requirements.uppercase ? "requirement met" : "requirement"}>
                En az 1 büyük harf
              </li>
              <li className={requirements.digit ? "requirement met" : "requirement"}>
                En az 1 rakam
              </li>
              <li className={requirements.special ? "requirement met" : "requirement"}>
                En az 1 özel karakter (!@#$%^&*)
              </li>
            </ul>
          </div>

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <button type="submit" className="update-button" disabled={loading}>
            {loading ? 'Güncelleniyor...' : 'Şifreyi Güncelle'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default NewPassword; 