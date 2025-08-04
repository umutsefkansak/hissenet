import React from 'react';
import './WithdrawForm.css';

const WithdrawForm = ({ 
  amount, 
  setAmount, 
  selectedBank, 
  setSelectedBank,
  iban,
  setIban,
  ibanConfirmed,
  setIbanConfirmed,
  walletBalance,
  loading 
}) => {
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(value);
  };

  const banks = [
    'Ziraat Bankası',
    'Garanti BBVA',
    'İş Bankası',
    'Yapı Kredi',
    'Akbank',
    'VakıfBank',
    'Halkbank',
    'Denizbank',
    'QNB Finansbank',
    'Türkiye Finans'
  ];

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
          max={walletBalance}
          required
          disabled={loading}
        />
        <div className="form-note">
          Maksimum: {formatCurrency(walletBalance)}
        </div>
      </div>

      <div className="form-section">
        <label>Banka Seçimi</label>
        <select
          value={selectedBank}
          onChange={(e) => setSelectedBank(e.target.value)}
          required
          disabled={loading}
        >
          <option value="">Bankanızı seçiniz</option>
          {banks.map((bank) => (
            <option key={bank} value={bank}>{bank}</option>
          ))}
        </select>
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
        <div className="checkbox-group">
          <input
            type="checkbox"
            id="ibanConfirm"
            checked={ibanConfirmed}
            onChange={(e) => setIbanConfirmed(e.target.checked)}
            disabled={loading}
          />
          <label htmlFor="ibanConfirm">
            IBAN bana aittir, onaylıyorum
          </label>
        </div>
      </div>
    </div>
  );
};

export default WithdrawForm;