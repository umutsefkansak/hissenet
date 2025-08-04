import React from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import Navbar from './components/Layout/Sidebar/Navbar';
import Home from './pages/Home';                   
import Login from './pages/Login';                  
import ForgotPassword from './pages/ForgotPassword'; 
import VerificationCode from './pages/VerificationCode'; 
import NewPassword from './pages/NewPassword';      
import Wallet from './pages/Wallet';                 
import ToastContainer from './components/Toast/ToastContainer';
import CustomerList from './pages/CustomerList/CustomerList';
import CustomerDetail from './pages/CustomerDetail/CustomerDetail';
import './App.css';

function AppContent() {
  const location = useLocation();

  return (
    <div className="App">
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/verification-code" element={<VerificationCode />} />
        <Route path="/new-password" element={<NewPassword />} />
        <Route path="/new-customer" element={<div className="page-content">Yeni Müşteri Sayfası</div>} />
        <Route path="/customer-transactions" element={<div className="page-content">Müşteri İşlemleri Sayfası</div>} />
        <Route path="/reports" element={<div className="page-content">Raporlar Sayfası</div>} />
        <Route path="/user-management" element={<div className="page-content">Kullanıcı Yönetimi Sayfası</div>} />
        <Route path="/wallet" element={<Wallet />} />
        <Route path="/customers" element={<CustomerList />} />
        <Route path="/customers/:id" element={<CustomerDetail />} />
      </Routes>
      <ToastContainer />
    </div>
  );
}

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;
