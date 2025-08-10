import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../../server/auth';
import logo from '../../images/logo-transparan1.png';
import './Login.css';

const Login = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const result = await login(formData.email, formData.password);

      if (result.success) {
        // Store personnel ID in localStorage
        if (result.data && result.data.response && result.data.response.id) {
          localStorage.setItem('personnelId', result.data.response.id.toString());
        }

        // Show success toast
        window.showToast('Başarıyla giriş yapıldı!', 'success', 2000);
        // Trigger custom event to notify navbar about login state change
        window.dispatchEvent(new Event('loginStateChanged'));
        // Redirect to home page on successful login
        setTimeout(() => {
          navigate('/');
        }, 1000);
      } else {
        setError(result.error || 'Email veya şifre yanlış');
      }
    } catch (error) {
      setError('Bir hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  return (
      <div className="login-container">
        <div className="login-card">
          {/* Logo ve Form */}
          <div className="form-section">
            <div className="brand-section">
              <div className="logo-container">
                <img src={logo} alt="HisseNet Logo" className="logo-image" />
              </div>
              <h1 className="login-title">Personel Girişi</h1>
              <p className="login-subtitle">Lütfen giriş bilgilerinizi giriniz</p>
            </div>

          {/* Login Form */}
            <form className="login-form" onSubmit={handleSubmit}>
              <div className="form-group">
                <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                    className="form-input"
                />
              </div>

              <div className="form-group password-group">
                <input
                    type={showPassword ? "text" : "password"}
                    name="password"
                    placeholder="Şifre"
                    value={formData.password}
                    onChange={handleChange}
                    required
                    className="form-input"
                />
                <button
                    type="button"
                    className="password-toggle"
                    onClick={togglePasswordVisibility}
                >
                  <svg className="eye-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    {showPassword ? (
                        <>
                          <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                          <circle cx="12" cy="12" r="3"/>
                        </>
                    ) : (
                        <path
                            d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                    )}
                  </svg>
                </button>
              </div>

              {error && (
                  <div className="error-message">
                    {error}
                  </div>
              )}


              <button
                  type="submit"
                  className="login-button"
                  disabled={loading}
              >
                {loading ? 'Giriş yapılıyor...' : 'Giriş'}
              </button>
              <div className="form-options">
                <Link to="/forgot-password" className="forgot-link">
                  Şifremi unuttum
                </Link>
              </div>
            </form>
          </div>
        </div>
      </div>
  );
};

export default Login;