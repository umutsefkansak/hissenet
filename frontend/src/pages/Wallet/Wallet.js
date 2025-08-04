import React, { useState, useEffect } from 'react';
import { WalletBalance, DepositForm, WithdrawForm } from '../../components/wallet';
import './Wallet.css';

const Wallet = () => {
  const [activeTab, setActiveTab] = useState('deposit');
  const [amount, setAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [walletBalance, setWalletBalance] = useState(15420.50);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const [customerId, setCustomerId] = useState(null);
  const [selectedBank, setSelectedBank] = useState('');
  const [iban, setIban] = useState('');
  const [ibanConfirmed, setIbanConfirmed] = useState(false);

  useEffect(() => {
    const storedCustomerId = localStorage.getItem('customerId');
    if (storedCustomerId) {
      setCustomerId(parseInt(storedCustomerId));
    } else {
      setCustomerId(46);
      localStorage.setItem('customerId', '46');
    }
  }, []);

  useEffect(() => {
    if (customerId) {
      fetchWalletBalance();
    }
  }, [customerId]);

  const fetchWalletBalance = async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/v1/wallet/customer/${customerId}/balance`);
      if (response.ok) {
        const data = await response.json();
        setWalletBalance(data.data);
      } else {
        console.error('Bakiye getirilemedi:', response.status);
        setMessage('Bakiye bilgisi alınamadı');
        setMessageType('error');
      }
    } catch (error) {
      console.error('Bakiye getirilemedi:', error);
      setMessage('Bakiye bilgisi alınamadı');
      setMessageType('error');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!amount || parseFloat(amount) <= 0) {
      setMessage('Lütfen geçerli bir tutar girin');
      setMessageType('error');
      return;
    }

    if (activeTab === 'withdraw') {
      if (!selectedBank || !iban || !ibanConfirmed) {
        setMessage('Lütfen tüm alanları doldurun ve IBAN onayını verin');
        setMessageType('error');
        return;
      }
    }

    setLoading(true);
    setMessage('');

    try {
      const endpoint = activeTab === 'deposit' 
        ? `/api/v1/wallet/customer/${customerId}/deposit`
        : `/api/v1/wallet/customer/${customerId}/withdrawal`;

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `amount=${amount}`
      });

      if (response.ok) {
        const data = await response.json();
        setMessage(activeTab === 'deposit' ? 'Para başarıyla yüklendi!' : 'Para çekme talebi gönderildi!');
        setMessageType('success');
        setAmount('');
        setSelectedBank('');
        setIban('');
        setIbanConfirmed(false);
        fetchWalletBalance();
      } else {
        const errorData = await response.json();
        setMessage(errorData.detail || 'İşlem başarısız');
        setMessageType('error');
      }
    } catch (error) {
      console.error('İşlem hatası:', error);
      setMessage('Bir hata oluştu');
      setMessageType('error');
    } finally {
      setLoading(false);
    }
  };

  if (!customerId) {
    return (
      <div className="wallet-page">
        <div className="wallet-container">
          <h1>Müşteri Bilgisi Gerekli</h1>
          <p>Lütfen önce giriş yapın veya müşteri ID'si belirtin.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="wallet-page">
      <div className="wallet-modal">
        {/* Header */}
        <div className="modal-header">
          <div className="tab-buttons">
            <button 
              className={`tab-button ${activeTab === 'withdraw' ? 'active' : ''}`}
              onClick={() => setActiveTab('withdraw')}
            >
              Bakiye Çek
            </button>
            <button 
              className={`tab-button ${activeTab === 'deposit' ? 'active' : ''}`}
              onClick={() => setActiveTab('deposit')}
            >
              Bakiye Yükle
            </button>
          </div>
          <button className="close-button">×</button>
        </div>

        {/* Content */}
        <div className="modal-content">
          {/* Bakiye Bilgileri */}
          <WalletBalance balance={walletBalance} />

          {/* İşlem Formu */}
          <form onSubmit={handleSubmit}>
            {activeTab === 'deposit' ? (
              <DepositForm 
                amount={amount}
                setAmount={setAmount}
                loading={loading}
              />
            ) : (
              <WithdrawForm 
                amount={amount}
                setAmount={setAmount}
                selectedBank={selectedBank}
                setSelectedBank={setSelectedBank}
                iban={iban}
                setIban={setIban}
                ibanConfirmed={ibanConfirmed}
                setIbanConfirmed={setIbanConfirmed}
                walletBalance={walletBalance}
                loading={loading}
              />
            )}

            {/* Mesaj */}
            {message && (
              <div className={`message ${messageType}`}>
                {message}
              </div>
            )}

            {/* Onay Butonu */}
            <button 
              type="submit" 
              className="confirm-button"
              disabled={loading}
            >
              {loading ? 'İşleniyor...' : 'Onayla'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Wallet;