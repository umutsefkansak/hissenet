import React from 'react';
import './WithdrawForm.css';

const WithdrawForm = ({ 
  amount, 
  setAmount, 
  iban,
  setIban,
  walletBalance,
  loading 
}) => {
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(value);
  };

  const calculateBlockedBalance = () => {
    return walletBalance * 0.20; // %20 bloke bakiye
  };

  const getAvailableBalance = () => {
    return walletBalance - calculateBlockedBalance();
  };

  return (
    <div className="withdraw-form">
      <div className="form-section">
        <label>Çekilecek Tutar (TL)</label>
        <input
          type="number"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="0.00"
          step="0.01"
          min="0.01"
          max={getAvailableBalance()}
          required
          disabled={loading}
        />
        <div className="form-note">
          Maksimum: {formatCurrency(getAvailableBalance())}
        </div>
      </div>

      <div className="form-section">
        <label>IBAN</label>
        <input
          type="text"
          value={iban}
          onChange={(e) => setIban(e.target.value)}
          placeholder="TR00 0000 0000 0000 0000 0000 00"
          required
          disabled={loading}
        />
        <div className="form-note">
          IBAN numaranızı boşluksuz ya da boşluklu girebilirsiniz
        </div>
      </div>
    </div>
  );
};

export default WithdrawForm;