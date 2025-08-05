import React from 'react';
import './WalletBalance.css';

const WalletBalance = ({ balance, currency = 'TRY' }) => {
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: currency
    }).format(value);
  };

  const calculateBlockedBalance = () => {
    return balance * 0.20; // %20 bloke bakiye
  };

  const getAvailableBalance = () => {
    return balance - calculateBlockedBalance();
  };

  return (
    <div className="wallet-balance">
      <h3>Bakiye Bilgileri</h3>
      <div className="balance-row">
        <span>Toplam Bakiye:</span>
        <span className="balance-amount">{formatCurrency(balance)}</span>
      </div>
      <div className="balance-row">
        <span>Bloke Bakiye (%20):</span>
        <span className="blocked-balance">{formatCurrency(calculateBlockedBalance())}</span>
      </div>
      <div className="balance-row">
        <span>Kullanılabilir Bakiye:</span>
        <span className="available-balance">{formatCurrency(getAvailableBalance())}</span>
      </div>
    </div>
  );
};

export default WalletBalance;