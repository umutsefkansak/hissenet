import React from 'react';
import './WithdrawForm.css';
const WithdrawForm = ({ amount, setAmount, iban, setIban, availableBalance, loading }) => {
  return (
    <div className="form-section">
      <label htmlFor="amount">Çekilecek Tutar (₺)</label>
      <input
        type="number"
        id="amount"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
        placeholder="Tutar girin"
        disabled={loading}
        min="0"
        step="0.01"
      />
      <div className="form-note">
        Maksimum çekilebilir tutar: {availableBalance.toLocaleString('tr-TR')} ₺
      </div>
      
      <div className="form-section">
        <label htmlFor="iban">IBAN</label>
        <input
          type="text"
          id="iban"
          value={iban}
          onChange={(e) => setIban(e.target.value)}
          placeholder="TR00 0000 0000 0000 0000 0000 00"
          disabled={loading}
        />
      </div>
    </div>
  );
};

export default WithdrawForm;