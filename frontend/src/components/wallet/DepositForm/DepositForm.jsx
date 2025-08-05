import React from 'react';
import './DepositForm.css';

const DepositForm = ({ amount, setAmount, loading, onSubmit }) => {
  return (
    <div className="deposit-form">
      <div className="form-section">
        <label>Yüklenecek Tutar (TL)</label>
        <input
          type="number"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="0.00"
          step="0.01"
          min="10"
          required
          disabled={loading}
        />
        <div className="form-note">
          Minimum yükleme tutarı: 10.00 TL
        </div>
      </div>

      <div className="info-section">
        <h4>Bakiye Yükleme Bilgileri</h4>
        <ul>
          <li>Bakiye yükleme işlemi anında gerçekleşir</li>
          <li>Minimum yükleme tutarı 10 TL'dir</li>
          <li>Maksimum günlük yükleme limiti 50.000 TL'dir</li>
          <li>Yüklenen tutar hesabınıza hemen aktarılır</li>
        </ul>
      </div>
    </div>
  );
};

export default DepositForm;