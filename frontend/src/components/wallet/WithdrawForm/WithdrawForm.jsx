import React, { useState, useEffect } from 'react';
import './WithdrawForm.css';
import { getCustomerById } from '../../../server/customer';

const WithdrawForm = ({ amount, setAmount, iban, setIban, availableBalance, loading, customerId }) => {
  const [customerName, setCustomerName] = useState('');

  useEffect(() => {
    if (customerId) {
      fetchCustomerInfo();
    }
  }, [customerId]);

  const fetchCustomerInfo = async () => {
    try {
      const response = await getCustomerById(customerId);
      if (response.data) {
        const customer = response.data;
        if (customer.customerType === 'INDIVIDUAL') {
          setCustomerName(`${customer.firstName} ${customer.lastName}`);
        } else {
          setCustomerName(customer.companyName);
        }
      }
    } catch (error) {
      console.error('Müşteri bilgileri alınamadı:', error);
    }
  };

  // IBAN formatı için yardımcı fonksiyon
  const formatIBAN = (value) => {
    // Sadece rakamları al
    const numbers = value.replace(/[^0-9]/g, '');
    
    // TR ile başla ve sayıları formatla
    let formatted = 'TR';
    
    if (numbers.length > 0) {
      formatted += ' ' + numbers.substring(0, 2); // 2 kontrol
    }
    if (numbers.length > 2) {
      formatted += ' ' + numbers.substring(2, 6); // 4 banka
    }
    if (numbers.length > 6) {
      const accountPart = numbers.substring(6, 22); // 16 hesap
      // Hesap numarasını 4'lü gruplar halinde böl
      const groups = accountPart.match(/.{1,4}/g) || [];
      formatted += ' ' + groups.join(' ');
    }
    
    return formatted;
  };

  const handleIBANChange = (e) => {
    const value = e.target.value;
    const formatted = formatIBAN(value);
    setIban(formatted);
  };

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
          onChange={handleIBANChange}
          placeholder="TR 00 0000 0000 0000 0000 0000 00"
          disabled={loading}
          maxLength={32}
        />
        {customerName && (
          <div className="customer-name-note">
            {customerName.toUpperCase()}
          </div>
        )}
      </div>
    </div>
  );
};

export default WithdrawForm;