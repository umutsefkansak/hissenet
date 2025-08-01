import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../server/api';
import logo from '../images/logo.png';
import './Login.css';

const Login = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

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
        // Show success toast
        window.showToast('Başarıyla giriş yapıldı!', 'success', 2000);
        // Trigger custom event to notify navbar about login state change
        window.dispatchEvent(new Event('loginStateChanged'));
        // Redirect to home page on successful login
        setTimeout(() => {
          navigate('/');
        }, 1000);
      } else {
        setError(result.error || 'Giriş başarısız');
      }
    } catch (error) {
      setError('Bir hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <div className="logo-container">
            <img src={logo} alt="HisseNet Logo" className="logo-image" />
          </div>
        </div>
        
        <form className="login-form" onSubmit={handleSubmit}>
          <h2 className="login-title">Giriş</h2>
          
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
          
          <div className="form-group">
            <input
              type="password"
              name="password"
              placeholder="Şifre"
              value={formData.password}
              onChange={handleChange}
              required
              className="form-input"
            />
          </div>
          
          {error && (
            <div className="error-message">
              {error}
            </div>
          )}
          
          <div className="forgot-password">
            <Link to="/forgot-password" className="forgot-link">
              Şifremi unuttum
            </Link>
          </div>
          
          <button type="submit" className="login-button" disabled={loading}>
            {loading ? 'Giriş yapılıyor...' : 'Giriş'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login; 