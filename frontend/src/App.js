import React from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import Navbar from './components/Layout/Sidebar/Navbar';
import Home from './pages/Home/Home';
import Login from './pages/Login/Login';
import ForgotPassword from './pages/ForgotPassword/ForgotPassword';
import VerificationCode from './pages/VerificationCode/VerificationCode';
import NewPassword from './pages/NewPassword/NewPassword';
import Wallet from './pages/Wallet';
import Portfolio from './pages/Portfolio/Portfolio';
import ToastContainer from './components/Toast/ToastContainer';
import IndividualCustomer from './pages/CreateCustomer/Individual/IndividualCustomer';
import CorporateCustomer from './pages/CreateCustomer/Corporate/CorporateCustomer';
import CustomerList from './pages/CustomerList/CustomerList';
import CustomerDetail from './pages/CustomerDetail/CustomerDetail';
import StocksPage from './pages/StocksPage/StocksPage';

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
        <Route path="/individual-customer" element={<IndividualCustomer />} />
        <Route path="/corporate-customer" element={<CorporateCustomer />} />
        <Route path="/customer-transactions" element={<div className="page-content">Müşteri İşlemleri Sayfası</div>} />
        <Route path="/reports" element={<div className="page-content">Raporlar Sayfası</div>} />
        <Route path="/user-management" element={<div className="page-content">Kullanıcı Yönetimi Sayfası</div>} />
        <Route path="/wallet" element={<Wallet />} />
        <Route path="/portfolio" element={<Portfolio />} />
        <Route path="/customers" element={<CustomerList />} />
        <Route path="/customers/:id" element={<CustomerDetail />} />
        <Route path="/stocks" element={<StocksPage/>} />
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
