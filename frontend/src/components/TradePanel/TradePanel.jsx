import React, { useState, useEffect } from 'react';
import styles from './TradePanel.module.css';
import { orderApi } from '../../server/order';
import { walletApi } from '../../server/wallet';

const TradePanel = ({ stock, onBack }) => {
  const [type, setType] = useState("BUY");
  const [category, setCategory] = useState("MARKET");
  const [quantity, setQuantity] = useState('');
  const [totalInput, setTotalInput] = useState('');
  const [price, setPrice] = useState('');
  const [availableQuantity, setAvailableQuantity] = useState(null);
  const [availableBalance, setAvailableBalance] = useState(null);

  const customerId = 68;

  const unitPrice = category === 'LIMIT' ? parseFloat(price) || 0 : stock.lastPrice || 0;
  const total = quantity && unitPrice ? (unitPrice * Number(quantity)).toFixed(2) : '0.00';
  const commission = (total * 0.005).toFixed(2);
  const net = (total - commission).toFixed(2);

  const formatPrice = (price) => {
    const number = parseFloat(price);
    return isNaN(number) ? '-' : number.toLocaleString('tr-TR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  };

  useEffect(() => {
    setPrice(stock.lastPrice?.toFixed(2) || '');
  }, [stock]);

  useEffect(() => {
    if (totalInput && unitPrice > 0) {
      const maxShares = Math.floor(parseFloat(totalInput) / unitPrice);
      setQuantity(maxShares.toString());
    }
  }, [totalInput, unitPrice]);

  useEffect(() => {
    const fetchAvailableQuantity = async () => {
      if (type === "SELL" && stock?.code) {
        try {
          const quantity = await orderApi.getQuantityForStockTransaction(customerId, stock.code);
          setAvailableQuantity(quantity);
          setQuantity(quantity.toString());
        } catch (error) {
          console.error("Mevcut hisse adedi alınamadı:", error);
          setAvailableQuantity(0);
          setQuantity('0');
        }
      } else {
        setAvailableQuantity(null);
        setQuantity('');
      }
    };

    fetchAvailableQuantity();
  }, [stock, type]);

  useEffect(() => {
    const fetchBalance = async () => {
      if (type === "BUY") {
        try {
          const result = await walletApi.getAvailableBalance(customerId);
          setAvailableBalance(result.data); // 🔥 burada düzeltildi
        } catch (err) {
          console.error("Bakiye alınamadı", err);
          setAvailableBalance(null);
        }
      }
    };

    fetchBalance();
  }, [type]);

  const handleSubmit = async () => {
    if (!customerId || !stock?.code || !quantity || Number(quantity) <= 0) {
      alert("Lütfen tüm alanları doğru doldurun.");
      return;
    }

    if (type === "SELL" && availableQuantity !== null && Number(quantity) > availableQuantity) {
      alert(`❌ En fazla ${availableQuantity} adet satabilirsiniz.`);
      return;
    }

    const payload = {
      customerId: Number(customerId),
      stockCode: stock.code,
      quantity: Number(quantity),
      price: category === "MARKET" ? stock.lastPrice : Number(price),
      type,
      category
    };

    try {
      await orderApi.createOrder(payload);
      alert("✅ Emir başarıyla gönderildi");
      setQuantity('');
      setTotalInput('');
    } catch (err) {
      alert("❌ Emir gönderilemedi: " + (err.message || err));
      console.error(err);
    }
  };

  return (
    <div className={styles.panel}>
      <div className={styles.topBar}>
        <button className={styles.backBtn} onClick={onBack}>&larr;</button>
        <h3 className={styles.title}>{stock.code} - {stock.text}</h3>
        <div className={styles.currentPrice}>{formatPrice(stock.lastPrice)}</div>
      </div>

      <div className={styles.tabs}>
        <div
          className={`${styles.tab} ${type === 'BUY' ? styles.activeTab : ''}`}
          onClick={() => setType('BUY')}
        >
          Alış
        </div>
        <div
          className={`${styles.tab} ${type === 'SELL' ? styles.activeTab : ''}`}
          onClick={() => setType('SELL')}
        >
          Satış
        </div>
      </div>

      <div className={styles.card}>
        <h3 className={styles.cardTitle}>
          {type === 'BUY' ? 'Alış Emri' : 'Satış Emri'}
        </h3>

        {type === 'BUY' && (
          <div className={styles.balanceInfo}>
            <strong>Mevcut Bakiye:</strong> {availableBalance !== null ? `${formatPrice(availableBalance)} TL` : 'Yükleniyor...'}
          </div>
        )}

        <div className={styles.field}>
          <label>Hisse Adedi</label>
          <input
            type="number"
            min="0"
            value={quantity}
            onChange={e => {
              const val = Number(e.target.value);
              if (val < 0) return;
              if (type === "SELL" && availableQuantity !== null && val > availableQuantity) {
                alert(`⚠️ Maksimum ${availableQuantity} adet satabilirsiniz.`);
                setQuantity(availableQuantity.toString());
              } else {
                setQuantity(e.target.value);
              }
            }}
            placeholder="Adet"
          />
        </div>

        <div className={styles.field}>
          <label>Toplam Tutar (TL)</label>
          <input
            type="number"
            min="0"
            value={totalInput}
            onChange={e => {
              const value = parseFloat(e.target.value);
              if (value < 0) return;

              if (type === "SELL" && category === "LIMIT" && availableQuantity !== null) {
                const maxTotal = availableQuantity * unitPrice;
                if (value > maxTotal) {
                  alert(`⚠️ Maksimum ${maxTotal.toFixed(2)} TL değerinde satış yapabilirsiniz.`);
                  setTotalInput(maxTotal.toFixed(2));
                  return;
                }
              }

              setTotalInput(e.target.value);
            }}
            placeholder="Toplam Tutar"
          />
        </div>

        <div className={styles.field}>
          <label>Emir Türü</label>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value === 'LIMIT' ? 'LIMIT' : 'MARKET')}
          >
            <option value="MARKET">Piyasa Emri</option>
            <option value="LIMIT">Limit Emri</option>
          </select>
        </div>

        {category === 'LIMIT' && (
          <div className={styles.field}>
            <label>Birim Fiyat</label>
            <input
              type="number"
              min="0"
              value={price}
              onChange={e => setPrice(e.target.value)}
              placeholder="TL"
            />
          </div>
        )}

        <div className={styles.summary}>
          <div><span>Birim Fiyat:</span><span>{formatPrice(unitPrice)}</span></div>
          <div><span>Adet:</span><span>{quantity || '-'}</span></div>
          <div><span>Toplam Tutar:</span><span>{total} TL</span></div>
          <div><span>Komisyon (%0.5):</span><span>{commission} TL</span></div>
          <div><span>Net Tutar:</span><span>{net} TL</span></div>
        </div>

        <button className={styles.actionBtn} onClick={handleSubmit}>
          {type === 'BUY' ? 'Alış Emri Ver' : 'Satış Emri Ver'}
        </button>
      </div>
    </div>
  );
};

export default TradePanel;
