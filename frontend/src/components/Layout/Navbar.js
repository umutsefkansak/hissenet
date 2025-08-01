import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { logout } from '../../server/api';
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    // Check if user is logged in
    const loginStatus = localStorage.getItem('isLogin');
    setIsLoggedIn(loginStatus === 'true');

    // Listen for storage changes (when login/logout happens in other components)
    const handleStorageChange = (e) => {
      if (e.key === 'isLogin') {
        setIsLoggedIn(e.newValue === 'true');
      }
    };

    window.addEventListener('storage', handleStorageChange);

    // Also listen for custom events
    const handleLoginChange = () => {
      const loginStatus = localStorage.getItem('isLogin');
      setIsLoggedIn(loginStatus === 'true');
    };

    window.addEventListener('loginStateChanged', handleLoginChange);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
      window.removeEventListener('loginStateChanged', handleLoginChange);
    };
  }, []);

  const handleLogout = async () => {
    try {
      await logout();
      setIsLoggedIn(false);
      // Show success toast
      window.showToast('Başarıyla çıkış yapıldı!', 'info', 2000);
      // Trigger custom event to notify other components about logout
      window.dispatchEvent(new Event('loginStateChanged'));
      navigate('/');
    } catch (error) {
      console.error('Logout error:', error);
      window.showToast('Çıkış yapılırken hata oluştu!', 'error', 3000);
    }
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-logo">
          HISSENET
        </Link>
        <ul className="nav-menu">
          {isLoggedIn ? (
            <li className="nav-item">
              <button onClick={handleLogout} className="nav-link logout-btn">
                Çıkış Yap
              </button>
            </li>
          ) : (
            <li className="nav-item">
              <Link to="/login" className="nav-link">
                Giriş Yap
              </Link>
            </li>
          )}
        </ul>
      </div>
    </nav>
  );
};

export default Navbar; 