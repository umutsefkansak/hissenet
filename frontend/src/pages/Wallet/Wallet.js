import React, { useState, useEffect } from 'react';
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
          <div className="balance-info">
            <h3>Bakiye Bilgileri</h3>
            <div className="balance-row">
              <span>Toplam Bakiye:</span>
              <span className="balance-amount">{formatCurrency(walletBalance)}</span>
            </div>
          </div>

          {/* İşlem Formu */}
          <form onSubmit={handleSubmit}>
            {activeTab === 'deposit' ? (
              // Para Yükleme Formu
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
            ) : (
              // Para Çekme Formu
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