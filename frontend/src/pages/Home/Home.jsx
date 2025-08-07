import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getAllEmployees } from '../../server/employees';
import logo from '../../images/logo3.jpeg'
import './Home.css';
import Dashboard from '../Dashboard/Dashboard';

const Home = () => {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [showWelcomeModal, setShowWelcomeModal] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // Check if user is logged in
    const loginStatus = localStorage.getItem('isLogin');
    setIsLoggedIn(loginStatus === 'true');
    setShowWelcomeModal(loginStatus !== 'true');

    // Listen for login state changes
    const handleLoginChange = () => {
      const loginStatus = localStorage.getItem('isLogin');
      setIsLoggedIn(loginStatus === 'true');
      setShowWelcomeModal(loginStatus !== 'true');
    };

    window.addEventListener('loginStateChanged', handleLoginChange);

    return () => {
      window.removeEventListener('loginStateChanged', handleLoginChange);
    };
  }, []);

  useEffect(() => {
    const fetchEmployees = async () => {
      setLoading(true);
      const result = await getAllEmployees();
      if (result.success) {
        setEmployees(result.data.data || []);
      }
      setLoading(false);
    };
    fetchEmployees();
  }, []);

  // Modal ve blur efektli overlay
  let modalMessage = "Tüm özelliklere erişmek için giriş yapın";
  if (!isLoggedIn && localStorage.getItem('isLogin') === 'false') {
    modalMessage = "Oturum süreniz doldu. Lütfen tekrar giriş yapınız.";
  }
  
  return (
    <div className="home">
      {!isLoggedIn ? (
        // Login durumunda sadece giriş butonu
        <div className="home-content">
          <div className="home-buttons">
            <Link to="/login" className="btn btn-primary">
              Giriş Yap
            </Link>
          </div>
        </div>
      ) : (
        // Giriş yapıldığında Dashboard tüm ekranı kaplasın
        <Dashboard />
      )}
      
      {/* Welcome Modal */}
      {showWelcomeModal && (
        <div className="welcome-modal-overlay">
          <div className="welcome-modal-card">
            <h2 className="welcome-title">Hissenet'e Hoşgeldin</h2>
            <p className="welcome-desc">{modalMessage}</p>
            <button
              className="welcome-login-btn"
              onClick={() => navigate('/login')}
            >
              Giriş Yap
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Home; 