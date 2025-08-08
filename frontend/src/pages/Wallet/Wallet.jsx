import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { WalletBalance, DepositForm, WithdrawForm } from '../../components/wallet';
import { walletApi } from '../../server/wallet';
import styles from './Wallet.module.css';

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
      fetchWalletData(); 
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
      <div className={styles.walletPage}>
        <div className={styles.walletContainer}>
          <h1 className={styles.walletContainerTitle}>Müşteri Bilgisi Gerekli</h1>
          <p className={styles.walletContainerText}>Lütfen önce giriş yapın veya müşteri ID'si belirtin.</p>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.walletPage}>
      <div className={styles.walletModal}>
        
        <div className={styles.modalHeader}>
          <div className={styles.walletTabButtons}>
            <button 
              className={`${styles.walletTabButton} ${activeTab === 'withdraw' ? styles.walletTabButtonActive : ''}`}
              onClick={() => setActiveTab('withdraw')}
            >
              Bakiye Çek
            </button>
            <button 
              className={`${styles.walletTabButton} ${activeTab === 'deposit' ? styles.walletTabButtonActive : ''}`}
              onClick={() => setActiveTab('deposit')}
            >
              Bakiye Yükle
            </button>
          </div>
        </div>

        <div className={styles.modalContent}>
          
          <WalletBalance 
            balance={walletBalance} 
            availableBalance={availableBalance}
            blockedBalance={blockedBalance}
          />

          <form onSubmit={handleSubmit} className={styles.modalContentForm}>
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
              <div className={`${styles.message} ${messageType === 'success' ? styles.messageSuccess : styles.messageError}`}>
                {message}
              </div>
            )}

            <button 
              type="submit" 
              className={styles.confirmButton}
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