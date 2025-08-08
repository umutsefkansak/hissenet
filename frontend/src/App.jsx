import React from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation, useNavigate } from 'react-router-dom';
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
import DashBoard from './pages/Dashboard/Dashboard'
import ModalDemo from './pages/ModalDemo/ModalDemo';
import TransactionHistory  from './pages/TransactionHistory/TransactionHistory';
import { useState, useEffect } from 'react';

import './App.css';
import EmployeeManagementPage from "./pages/EmployeeManagement/EmployeeManagementPage";
import Reports from './pages/Reports/Reports';

function AppContent() {
  const location = useLocation();
  // Drawer'ın gözükmeyeceği sayfalar
  const authPages = ['/login', '/forgot-password', '/verification-code', '/new-password'];
  const isAuthPage = authPages.includes(location.pathname);

  return (
    <div className={`App ${isAuthPage ? 'auth-page' : ''}`}>  
      {!isAuthPage && <Navbar />}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/verification-code" element={<VerificationCode />} />
        <Route path="/new-password" element={<NewPassword />} />
        <Route path="/new-customer" element={<div className="page-content">Yeni Müşteri Sayfası</div>} />
        <Route path="/individual-customer" element={<IndividualCustomer />} />
        <Route path="/corporate-customer" element={<CorporateCustomer />} />
        <Route path="/reports" element={<Reports></Reports>} />
        <Route path="/customer-transactions" element={<div className="page-content">Müşteri İşlemleri Sayfası</div>} />
        <Route path="/reports" element={<Reports />} />
        <Route path="/employee-management" element={<EmployeeManagementPage />} />
        <Route path="/wallet/:customerId?" element={<Wallet />} />
        <Route path="/portfolio/:customerId?" element={<Portfolio />} />
        <Route path="/customers" element={<CustomerList />} />
        <Route path="/customers/:id" element={<CustomerDetail />} />
        <Route path="/stocks" element={<StocksPage/>} />
        <Route path="/dashboard" element={<DashBoard/>} />
        <Route path="/popup" element={<ModalDemo/>} />
        <Route path="/transaction-history" element={<TransactionHistory/>} />
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
