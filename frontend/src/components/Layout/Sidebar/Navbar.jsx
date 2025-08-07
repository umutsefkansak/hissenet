import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { logout } from '../../../server/auth';
import logo from '../../../images/logo-white.png';
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [isMobile, setIsMobile] = useState(false);
  const [isCustomerSubmenuOpen, setIsCustomerSubmenuOpen] = useState(false);


  const isActiveMenu = (path) => {
    return location.pathname === path;
  };

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


  useEffect(() => {
    if (location.pathname === '/individual-customer' || location.pathname === '/corporate-customer') {
      setIsCustomerSubmenuOpen(true);
    }
  }, [location.pathname]);

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
    setIsCustomerSubmenuOpen(false);
  };

  const toggleCustomerSubmenu = () => {
    setIsCustomerSubmenuOpen(!isCustomerSubmenuOpen);
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
                <>
                  <ul className="drawer-menu-list">
                    <li className={`drawer-menu-item ${isActiveMenu('/') ? 'active' : ''}`}>
                      <Link to="/" className="drawer-menu-link" onClick={closeDrawer}>
                        <div className="drawer-menu-header">
                          <div className="drawer-menu-left">
                        <span className="drawer-menu-icon">
                          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <polyline points="9,22 9,12 15,12 15,22" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                          </svg>
                        </span>
                            <span className="drawer-menu-text">Ana Sayfa</span>
                          </div>
                        </div>
                      </Link>
                    </li>

                    <li className={`drawer-menu-item ${isActiveMenu('/customers') ? 'active' : ''}`}>
                    <Link to="/customers" className="drawer-menu-link" onClick={closeDrawer}>
                        <div className="drawer-menu-header">
                          <div className="drawer-menu-left">
                        <span className="drawer-menu-icon">
                          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <circle cx="9" cy="7" r="4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <path d="M23 21V19C23 18.1326 22.7035 17.2982 22.1677 16.636C21.6319 15.9738 20.8918 15.5229 20.06 15.36" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <path d="M16 3.13C16.8318 3.29312 17.5719 3.74398 18.1077 4.40619C18.6435 5.06839 18.94 5.90285 18.94 6.77C18.94 7.63715 18.6435 8.47161 18.1077 9.13381C17.5719 9.79602 16.8318 10.2469 16 10.41" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                          </svg>
                        </span>
                            <span className="drawer-menu-text">Müşteri İşlemleri</span>
                          </div>
                        </div>
                      </Link>
                    </li>

                    <li className={`drawer-menu-item ${isActiveMenu('/individual-customer') || isActiveMenu('/corporate-customer') ? 'active' : ''}`}>
                      <div className="drawer-menu-link">
                        <div className="drawer-menu-header" onClick={toggleCustomerSubmenu}>
                          <div className="drawer-menu-left">
                        <span className="drawer-menu-icon">
                          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <circle cx="12" cy="7" r="4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                          </svg>
                        </span>
                            <span className="drawer-menu-text">Yeni Müşteri</span>
                          </div>
                          <span className={`drawer-submenu-arrow ${isCustomerSubmenuOpen ? 'open' : ''}`}>
                        {isCustomerSubmenuOpen ? (
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                              <path d="M6 9L12 15L18 9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            </svg>
                        ) : (
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                              <path d="M9 18L15 12L9 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            </svg>
                        )}
                      </span>
                        </div>
                        {isCustomerSubmenuOpen && (
                            <div className="drawer-submenu">
                              <Link to="/individual-customer" className={`drawer-submenu-link ${isActiveMenu('/individual-customer') ? 'active' : ''}`} onClick={closeDrawer}>
                                <span className="drawer-submenu-text">Bireysel</span>
                              </Link>
                              <Link to="/corporate-customer" className={`drawer-submenu-link ${isActiveMenu('/corporate-customer') ? 'active' : ''}`} onClick={closeDrawer}>
                                <span className="drawer-submenu-text">Kurumsal</span>
                              </Link>
                            </div>
                        )}
                      </div>
                    </li>

                    <li className={`drawer-menu-item ${isActiveMenu('/reports') ? 'active' : ''}`}>
                      <Link to="/reports" className="drawer-menu-link" onClick={closeDrawer}>
                        <div className="drawer-menu-header">
                          <div className="drawer-menu-left">
                        <span className="drawer-menu-icon">
                          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M3 3V21H21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <path d="M9 9L12 6L16 10L20 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                          </svg>
                        </span>
                            <span className="drawer-menu-text">Raporlar</span>
                          </div>
                        </div>
                      </Link>
                    </li>

                    <li className={`drawer-menu-item ${isActiveMenu('/user-management') ? 'active' : ''}`}>
                      <Link to="/user-management" className="drawer-menu-link" onClick={closeDrawer}>
                        <div className="drawer-menu-header">
                          <div className="drawer-menu-left">
                        <span className="drawer-menu-icon">
                          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <circle cx="12" cy="7" r="4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                          </svg>
                        </span>
                            <span className="drawer-menu-text">Kullanıcı Yönetimi</span>
                          </div>
                        </div>
                      </Link>
                    </li>
                  </ul>

                  <div className="drawer-footer">
                    <button onClick={handleLogout} className="drawer-logout-btn">
                  <span className="drawer-menu-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      <polyline points="16,17 21,12 16,7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      <line x1="21" y1="12" x2="9" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                  </span>
                      <span>Çıkış Yap</span>
                    </button>
                  </div>
                </>
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