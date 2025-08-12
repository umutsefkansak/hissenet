import React from 'react';
import './WalletBalance.css';

const WalletBalance = ({ balance, availableBalance, blockedBalance, currency = 'TRY' }) => {
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
      <div className="balance-row">
        <span>Bloke Bakiye:</span>
        <span className="blocked-balance">{formatCurrency(blockedBalance)}</span>
      </div>
      <div className="balance-row">
        <span>Kullanılabilir Bakiye:</span>
        <span className="available-balance">{formatCurrency(availableBalance)}</span>
      </div>
    </div>
  );
};

export default WalletBalance;