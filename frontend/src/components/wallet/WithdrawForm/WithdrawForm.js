import React from 'react';
import './WithdrawForm.css';

const WithdrawForm = ({ 
  amount, 
  setAmount, 
  walletBalance, 
  loading 
}) => {
  const handleAmountChange = (e) => {
    const value = e.target.value;
    if (value === '' || parseFloat(value) >= 0) {
      setAmount(value);
    }
  };

  const calculateBlockedBalance = () => {
    return walletBalance * 0.20; // %20 bloke bakiye
  };

  const getAvailableBalance = () => {
    return walletBalance - calculateBlockedBalance();
  };

  return (
    <div className="withdraw-form">
      {/* Bakiye Bilgileri */}
      <div className="balance-info">
        <div className="balance-item">
          <label>Toplam Bakiye:</label>
          <span className="total-balance">{walletBalance.toLocaleString('tr-TR')} ₺</span>
        </div>
        <div className="balance-item">
          <label>Bloke Bakiye (%20):</label>
          <span className="blocked-balance">{calculateBlockedBalance().toLocaleString('tr-TR')} ₺</span>
        </div>
        <div className="balance-item">
          <label>Kullanılabilir Bakiye:</label>
          <span className="available-balance">{getAvailableBalance().toLocaleString('tr-TR')} ₺</span>
        </div>
      </div>

      {/* Çekilecek Tutar */}
      <div className="form-section">
        <label htmlFor="amount">Çekilecek Tutar (TL):</label>
        <input
          type="number"
          id="amount"
          value={amount}
          onChange={handleAmountChange}
          placeholder="0.00"
          disabled={loading}
          max={getAvailableBalance()}
          min="0"
          step="0.01"
        />
        <div className="form-note">
          Maksimum: {getAvailableBalance().toLocaleString('tr-TR')} ₺
        </div>
      </div>
    </div>
  );
};

export default WithdrawForm;