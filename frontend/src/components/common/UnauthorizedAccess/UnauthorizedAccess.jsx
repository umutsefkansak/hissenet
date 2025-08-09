import React from 'react';
import { useNavigate } from 'react-router-dom';
import './UnauthorizedAccess.css';

const UnauthorizedAccess = ({ 
  title = "Yetkisiz Erişim", 
  message = "Bu sayfaya erişim yetkiniz bulunmamaktadır.",
  description = "Yalnızca kendi hesabınıza ait sayfalara erişebilirsiniz.",
  showLoginButton = false 
}) => {
  const navigate = useNavigate();

  return (
    <div className="unauthorized-page-container">
      <div className="unauthorized-content">
        <div className="unauthorized-icon">
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="#ef4444" strokeWidth="2"/>
            <path d="M4.93 4.93l14.14 14.14" stroke="#ef4444" strokeWidth="2" strokeLinecap="round"/>
          </svg>
        </div>
        <h1 className="unauthorized-code">403</h1>
        <h2 className="unauthorized-title">{title}</h2>
        <p className="unauthorized-message">{message}</p>
        <p className="unauthorized-description">
          {description}
        </p>
        <div className="unauthorized-actions">
          {showLoginButton ? (
            <button onClick={() => navigate('/login')} className="primary-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M15 3h6v18h-6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                <path d="M10 17l5-5-5-5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                <path d="M15 12H3" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
              Giriş Yap
            </button>
          ) : (
            <button onClick={() => navigate('/')} className="primary-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                <polyline points="9,22 9,12 15,12 15,22" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
              Ana Sayfaya Dön
            </button>
          )}
          <button onClick={() => navigate(-1)} className="secondary-btn">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19 12H5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              <path d="M12 19l-7-7 7-7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
            Geri Dön
          </button>
        </div>
      </div>
    </div>
  );
};

export default UnauthorizedAccess; 