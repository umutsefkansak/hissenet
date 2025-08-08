import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { WalletBalance, DepositForm, WithdrawForm } from '../../components/wallet';
import { walletApi } from '../../server/wallet';
import './Wallet.css';

const Wallet = () => {
  const { customerId = '68' } = useParams(); // Default to 68 if no parameter
  const [activeTab, setActiveTab] = useState('deposit');
  const [amount, setAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [walletBalance, setWalletBalance] = useState(0);
  const [availableBalance, setAvailableBalance] = useState(0);
  const [blockedBalance, setBlockedBalance] = useState(0);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const [iban, setIban] = useState(''); 

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
      const [balanceData, availableData, blockedData] = await Promise.all([
        walletApi.getWalletBalance(customerId),
        walletApi.getAvailableBalance(customerId),
        walletApi.getBlockedBalance(customerId)
      ]);

      setWalletBalance(balanceData.data);
      setAvailableBalance(availableData.data);
      setBlockedBalance(blockedData.data);
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
      if (activeTab === 'deposit') {
        await walletApi.deposit(customerId, amount);
        setMessage('Para başarıyla yüklendi!');
      } else {
        await walletApi.withdraw(customerId, amount);
        setMessage('Para çekme talebi gönderildi!');
      }
      
      setMessageType('success');
      setAmount('');
      setIban('');
      fetchWalletData(); // Bakiye bilgilerini yenile
    } catch (error) {
      console.error('İşlem hatası:', error);
      setMessage(error.message || 'Bir hata oluştu');
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
          <div className="wallet-tab-buttons">
            <button 
              className={`wallet-tab-button ${activeTab === 'withdraw' ? 'active' : ''}`}
              onClick={() => setActiveTab('withdraw')}
            >
              Bakiye Çek
            </button>
            <button 
              className={`wallet-tab-button ${activeTab === 'deposit' ? 'active' : ''}`}
              onClick={() => setActiveTab('deposit')}
            >
              Bakiye Yükle
            </button>
          </div>
        </div>

        <div className="modal-content">
          
          <WalletBalance 
            balance={walletBalance} 
            availableBalance={availableBalance}
            blockedBalance={blockedBalance}
          />

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
                customerId={customerId}
              />
            )}

            {message && (
              <div className={`message ${messageType}`}>
                {message}
              </div>
            )}

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