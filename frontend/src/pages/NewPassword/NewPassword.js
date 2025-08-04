import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { changePassword } from '../../server/api';
import './NewPassword.css';

const NewPassword = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const email = location.state?.email;

  // Email yoksa login sayfasına yönlendir
  React.useEffect(() => {
    if (!email) {
      window.showToast('Email bilgisi bulunamadı. Lütfen tekrar giriş yapın.', 'error', 3000);
      navigate('/login');
    }
  }, [email, navigate]);

  // Email yoksa sayfayı render etme
  if (!email) {
    return null;
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

  const requirements = validatePassword(password);

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
            Yeni şifrenizi oluşturun.
          </p>
        </div>

        <form className="new-password-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Yeni Şifre</label>
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
                {showPassword ? "👁️" : "👁️‍🗨️"}
              </button>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Şifreyi Onayla</label>
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
                {showConfirmPassword ? "👁️" : "👁️‍🗨️"}
              </button>
            </div>
          </div>

          <div className="password-requirements">
            <h3 className="requirements-title">Şifre gereksinimleri:</h3>
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