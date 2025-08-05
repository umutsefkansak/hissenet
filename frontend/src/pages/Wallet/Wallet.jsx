import React, { useState, useEffect } from 'react';
import { WalletBalance, DepositForm, WithdrawForm } from '../../components/wallet';
import './Wallet.css';

const Wallet = () => {
  const [activeTab, setActiveTab] = useState('deposit');
  const [amount, setAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [walletBalance, setWalletBalance] = useState(0);
  const [availableBalance, setAvailableBalance] = useState(0);
  const [blockedBalance, setBlockedBalance] = useState(0);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const [customerId, setCustomerId] = useState(null);
  const [iban, setIban] = useState(''); 

  useEffect(() => {
    const storedCustomerId = localStorage.getItem('customerId');
    if (storedCustomerId) {
      setCustomerId(parseInt(storedCustomerId));
    } else {
      setCustomerId(68);
      localStorage.setItem('customerId', '68');
    }
  }, []);

  useEffect(() => {
    if (customerId) {
      fetchWalletData();
    }
  }, [customerId]);

  useEffect(() => {
    setMessage('');
    setMessageType('');
  }, [activeTab]);

  const fetchWalletData = async () => {
    try {
      setLoading(true);
      
      // Tüm bakiye bilgilerini paralel olarak çek
      const [balanceResponse, availableResponse, blockedResponse] = await Promise.all([
        fetch(`/api/v1/wallet/customer/${customerId}/balance`),
        fetch(`/api/v1/wallet/customer/${customerId}/available-balance`),
        fetch(`/api/v1/wallet/customer/${customerId}/blocked-balance`)
      ]);

      if (balanceResponse.ok && availableResponse.ok && blockedResponse.ok) {
        const balanceData = await balanceResponse.json();
        const availableData = await availableResponse.json();
        const blockedData = await blockedResponse.json();

        setWalletBalance(balanceData.data);
        setAvailableBalance(availableData.data);
        setBlockedBalance(blockedData.data);
      } else {
        console.error('Bakiye bilgileri getirilemedi');
        setMessage('Bakiye bilgileri alınamadı');
        setMessageType('error');
      }
    } catch (error) {
      console.error('Bakiye bilgileri getirilemedi:', error);
      setMessage('Bakiye bilgileri alınamadı');
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

    // Gerçek available balance ile kontrol
    if (activeTab === 'withdraw') {
      if (!iban) {
        setMessage('Lütfen IBAN bilgisini girin');
        setMessageType('error');
        return;
      }
      
      if (parseFloat(amount) > availableBalance) {
        setMessage(`Çekilebilir tutar ${availableBalance.toLocaleString('tr-TR')} ₺'yi aşamaz`);
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
        setIban('');
        fetchWalletData(); // Bakiye bilgilerini yenile
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
          <WalletBalance 
            balance={walletBalance} 
            availableBalance={availableBalance}
            blockedBalance={blockedBalance}
          />

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
                iban={iban}
                setIban={setIban}
                availableBalance={availableBalance}
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