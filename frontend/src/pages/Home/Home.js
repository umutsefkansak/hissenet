import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllEmployees } from '../../server/api';
import logo from '../../images/logo3.jpeg'
import './Home.css';

const Home = () => {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    // Check if user is logged in
    const loginStatus = localStorage.getItem('isLogin');
    setIsLoggedIn(loginStatus === 'true');

    // Listen for login state changes
    const handleLoginChange = () => {
      const loginStatus = localStorage.getItem('isLogin');
      setIsLoggedIn(loginStatus === 'true');
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

  return (
    <div className="home">
      <div className="home-content">
        <div className="logo-container">
          <img src={logo} alt="HisseNet Logo" className="logo-image" />
        </div>
        <p>Finansal yönetim platformuna giriş yapın</p>
        {!isLoggedIn && (
          <div className="home-buttons">
            <Link to="/login" className="btn btn-primary">
              Giriş Yap
            </Link>
          </div>
        )}
        
        {/* Test: Employee List - Only show if logged in */}
        {isLoggedIn && (
          <div style={{ marginTop: '30px', textAlign: 'left' }}>
            <h3>Mevcut Çalışanlar (Test):</h3>
            {loading ? (
              <p>Yükleniyor...</p>
            ) : (
              <div>
                {employees.length > 0 ? (
                  employees.map((emp, index) => (
                    <div key={index} style={{ margin: '10px 0', padding: '10px', border: '1px solid #ddd', borderRadius: '5px' }}>
                      <strong>Email:</strong> {emp.email}<br/>
                      <strong>Ad:</strong> {emp.firstName} {emp.lastName}<br/>
                      <strong>Pozisyon:</strong> {emp.position}
                    </div>
                  ))
                ) : (
                  <p>Hiç çalışan bulunamadı</p>
                )}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Home; 