import React from 'react';
import './WalletBalance.css';

const WalletBalance = ({ balance, currency = 'TRY' }) => {
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: currency
    }).format(value);
  };

  return (
    <div className="wallet-balance">
      <h3>Bakiye Bilgileri</h3>
      <div className="balance-row">
        <span>Toplam Bakiye:</span>
        <span className="balance-amount">{formatCurrency(balance)}</span>
      </div>
    </div>
  );
};

export default WalletBalance;