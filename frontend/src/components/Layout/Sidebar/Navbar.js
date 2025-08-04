import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { logout } from '../../../server/api';
import logo from '../../images/logo3.jpeg';
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [isMobile, setIsMobile] = useState(false);

  useEffect(() => {
    // Check if user is logged in
    const loginStatus = localStorage.getItem('isLogin');
    setIsLoggedIn(loginStatus === 'true');

    // Check screen size
    const checkScreenSize = () => {
      setIsMobile(window.innerWidth <= 768);
      if (window.innerWidth > 768) {
        setIsDrawerOpen(true); // Desktop'ta her zaman açık
      } else {
        setIsDrawerOpen(false); // Mobile'da kapalı
      }
    };

    checkScreenSize();
    window.addEventListener('resize', checkScreenSize);

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
      window.removeEventListener('resize', checkScreenSize);
    };
  }, []);

  const handleLogout = async () => {
    try {
      await logout();
      setIsLoggedIn(false);
      if (isMobile) {
        setIsDrawerOpen(false);
      }
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

  const toggleDrawer = () => {
    if (isMobile) {
      setIsDrawerOpen(!isDrawerOpen);
    }
  };

  const closeDrawer = () => {
    if (isMobile) {
      setIsDrawerOpen(false);
    }
  };

  return (
    <>
      {/* Mobile Navbar */}
      <nav className="navbar">
        <div className="navbar-container">
          <Link to="/" className="navbar-logo">
            HISSENET
          </Link>
          
          {/* Desktop Menu */}
          <ul className="nav-menu desktop-menu">
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

          {/* Mobile Menu Button - Sağda */}
          <button className="mobile-menu-btn" onClick={toggleDrawer}>
            <span className={`hamburger ${isDrawerOpen ? 'active' : ''}`}></span>
          </button>
        </div>
      </nav>

      {/* Drawer Menu */}
      <div className={`drawer-overlay ${isDrawerOpen && isMobile ? 'active' : ''}`} onClick={closeDrawer}></div>
      <div className={`drawer-menu ${isDrawerOpen ? 'open' : ''}`}>
        <div className="drawer-header">
          <Link to="/" className="drawer-logo" onClick={closeDrawer}>
            <img src={logo} alt="HisseNet Logo" className="drawer-logo-img" />
            <span className="drawer-brand">HISSENET</span>
          </Link>
          {isMobile && (
            <button className="drawer-close" onClick={closeDrawer}>
              <span>×</span>
            </button>
          )}
        </div>

        <div className="drawer-content">
          {isLoggedIn ? (
            // Giriş yapmış kullanıcı menüsü
            <ul className="drawer-menu-list">
              <li className="drawer-menu-item">
                <Link to="/new-customer" className="drawer-menu-link" onClick={closeDrawer}>
                  <div className="drawer-menu-header">
                    <span className="drawer-menu-icon">+</span>
                    <span className="drawer-menu-text">Yeni Müşteri</span>
                  </div>
                </Link>
              </li>

              <li className="drawer-menu-item">
                <Link to="/customer-transactions" className="drawer-menu-link" onClick={closeDrawer}>
                  <div className="drawer-menu-header">
                    <span className="drawer-menu-icon">🔄</span>
                    <span className="drawer-menu-text">Müşteri İşlemleri</span>
                  </div>
                </Link>
              </li>

              <li className="drawer-menu-item">
                <Link to="/reports" className="drawer-menu-link" onClick={closeDrawer}>
                  <div className="drawer-menu-header">
                    <span className="drawer-menu-icon">📊</span>
                    <span className="drawer-menu-text">Raporlar</span>
                  </div>
                </Link>
              </li>

              <li className="drawer-menu-item">
                <Link to="/user-management" className="drawer-menu-link" onClick={closeDrawer}>
                  <div className="drawer-menu-header">
                    <span className="drawer-menu-text">Kullanıcı Yönetimi</span>
                  </div>
                </Link>
              </li>

              <li className="drawer-menu-item logout-item">
                <button onClick={handleLogout} className="drawer-logout-btn">
                  Çıkış Yap
                </button>
              </li>
            </ul>
          ) : (
            // Giriş yapmamış kullanıcı menüsü
            <ul className="drawer-menu-list">
              <li className="drawer-menu-item">
                <Link to="/login" className="drawer-login-link" onClick={closeDrawer}>
                  Giriş Yap
                </Link>
              </li>
            </ul>
          )}
        </div>
      </div>
    </>
  );
};

export default Navbar; 