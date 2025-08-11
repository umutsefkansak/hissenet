import React, { useState, useEffect, useMemo, useCallback } from 'react';
import styles from './TradePanel.module.css';
import { orderApi } from '../../server/order';
import { walletApi } from '../../server/wallet';
import { customerApi } from '../../server/customerApi';
import { sendMail } from '../../server/mail';
import Modal from '../../components/common/Modal/Modal';

const TradePanel = ({ stock, onBack }) => {
  const [type, setType] = useState("BUY");
  const [category, setCategory] = useState("MARKET");
  const [quantity, setQuantity] = useState('');
  const [totalInput, setTotalInput] = useState('');
  const [price, setPrice] = useState('');
  const [availableBalance, setAvailableBalance] = useState(null);
  const [sellableQty, setSellableQty] = useState(null);
  const [sellHold, setSellHold] = useState(0);
  const [commissionRate, setCommissionRate] = useState(0.005);
  const [modalConfig, setModalConfig] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const customerId = localStorage.getItem("customerId");

  const normalizeNumber = (val) => {
    const n = Number(val);
    return Number.isFinite(n) ? n : 0;
  };
  const normalizeApiNumber = (resp) => {
    if (typeof resp === 'number') return resp;
    if (resp && typeof resp.data === 'number') return resp.data;
    if (resp && resp.data != null) return normalizeNumber(resp.data);
    return normalizeNumber(resp);
  };
  const normalizeCommission = (val) => {
    const n = Number(val);
    if (!Number.isFinite(n) || n < 0) return 0.005;
    return n > 1 ? n / 100 : n;
  };
  const formatPrice = (p) => {
    const number = parseFloat(p);
    return isNaN(number) ? '-' : number.toLocaleString('tr-TR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  };

  const unitPrice = category === 'LIMIT' ? parseFloat(price) || 0 : (stock.lastPrice || 0);
  const totalNumber = quantity && unitPrice ? unitPrice * Number(quantity) : 0;
  const commissionNumber = totalNumber * commissionRate;
  const netNumber = totalNumber + commissionNumber;

  const total = totalNumber.toFixed(2);
  const commission = commissionNumber.toFixed(2);
  const net = netNumber.toFixed(2);

  const openModal = (config) => setModalConfig(config);
  const closeModal = () => setModalConfig(null);

  const buildConfirmMessage = () => {
    const lines = [
      `${stock.code} - ${stock.text}`,
      `İşlem: ${type === 'BUY' ? 'ALIŞ' : 'SATIŞ'}`,
      `Emir Türü: ${category === 'MARKET' ? 'Piyasa Emri' : 'Limit Emri'}`,
      `Birim Fiyat: ${formatPrice(unitPrice)} TL`,
      `Adet: ${quantity || '-'}`,
      `Toplam: ${total} TL`,
      `Komisyon (%${(commissionRate * 100).toFixed(2)}): ${commission} TL`,
      `Net: ${net} TL`
    ];
    return lines.join('\n');
  };

  const fetchSellableQty = useCallback(async () => {
    if (!stock?.code || !customerId) {
      setSellableQty(null);
      return;
    }
    try {
      const resp = await orderApi.getAvailableQuantity(customerId, stock.code);
      const q = Math.max(0, normalizeApiNumber(resp));
      setSellableQty(q);
    } catch {
      setSellableQty(0);
    }
  }, [customerId, stock?.code]);

  const fetchBalance = useCallback(async () => {
    if (!customerId) {
      setAvailableBalance(null);
      return;
    }
    try {
      const resp = await walletApi.getAvailableBalance(customerId);
      const val = normalizeApiNumber(resp);
      setAvailableBalance(val);
    } catch {
      setAvailableBalance(null);
    }
  }, [customerId]);

  const fetchCommissionRate = useCallback(async () => {
    if (!customerId) {
      setCommissionRate(0.005);
      return;
    }
    try {
      const data = await customerApi.getCustomerById(customerId);
      const cr = normalizeCommission(data?.data?.commissionRate || data?.commissionRate || 0.005);
      setCommissionRate(cr);
    } catch {
      setCommissionRate(0.005);
    }
  }, [customerId]);

  const displaySellable = useMemo(() => sellableQty, [sellableQty]);

  const effectiveSellable = useMemo(() => {
    const base = normalizeNumber(sellableQty);
    const hold = normalizeNumber(sellHold);
    return Math.max(0, base - hold);
  }, [sellableQty, sellHold]);

  useEffect(() => {
    setPrice(stock.lastPrice?.toFixed(2) || '');
  }, [stock]);

  useEffect(() => {
    if (totalInput && unitPrice > 0) {
      const maxShares = Math.floor(parseFloat(totalInput) / unitPrice);
      setQuantity(String(maxShares));
    }
  }, [totalInput, unitPrice]);

  useEffect(() => {
    fetchCommissionRate();
  }, [fetchCommissionRate]);

  useEffect(() => {
    fetchSellableQty();
    if (type === 'BUY') fetchBalance();
  }, [stock, type, fetchSellableQty, fetchBalance]);

  useEffect(() => {
    if (type === 'BUY') fetchBalance();
    if (type === 'SELL') fetchSellableQty();
  }, [type, fetchBalance, fetchSellableQty]);

  useEffect(() => {
    setSellHold((h) => {
      const base = normalizeNumber(sellableQty);
      return Math.min(base, normalizeNumber(h));
    });
  }, [sellableQty]);

  const handleSubmit = () => {
    if (!customerId || !stock?.code || !quantity || Number(quantity) <= 0) {
      openModal({
        variant: 'error',
        title: 'Eksik / Hatalı Bilgi',
        message: 'Lütfen tüm alanları doğru doldurun.',
        confirmText: 'Tamam',
        onClose: closeModal
      });
      return;
    }

    openModal({
      variant: 'confirm',
      title: type === 'BUY' ? 'Alış Emrini Onayla' : 'Satış Emrini Onayla',
      message: buildConfirmMessage(),
      cancelText: 'Vazgeç',
      confirmText: type === 'BUY' ? 'Onayla ve Al' : 'Onayla ve Sat',
      onConfirm: async () => {
        closeModal();
        setIsSubmitting(true);

        const payload = {
          customerId: Number(customerId),
          stockCode: stock.code,
          quantity: Number(quantity),
          price: category === 'MARKET' ? stock.lastPrice : Number(price),
          type,
          category
        };

        try {
          const res = await orderApi.createOrder(payload);
          const statusStrRaw = (res?.data?.status ?? res?.status ?? '').toString();
          const statusStr = statusStrRaw.toUpperCase();
          const idForTxn = res?.data?.transactionId || res?.data?.id;
          const txn = idForTxn ? `TXN-${new Date().getFullYear()}-${String(idForTxn).padStart(6,'0')}` : 'Bilinmiyor';

          try {
            const customer = await customerApi.getCustomerById(customerId);
            const to = customer?.email || customer?.data?.email || '';
            const firstName = customer?.firstName || customer?.data?.firstName || '';
            const lastName = customer?.lastName || customer?.data?.lastName || '';
            const companyName = customer?.companyName || customer?.data?.companyName || '';
            const recipientName = `${firstName} ${lastName}`.trim() || companyName || 'Müşterimiz';

            let subject = '';
            let content = '';

            if (statusStr === 'FILLED' || (category === 'MARKET' && statusStr !== 'REJECTED')) {
              subject = `Hisse ${type === 'BUY' ? 'Alım' : 'Satım'} İşlemi Gerçekleşti`;
              content = `
                <h2>${subject}</h2>
                <p>${stock.code} hissesinden ${quantity} adet ${type === 'BUY' ? 'alım' : 'satım'} işleminiz ${formatPrice(unitPrice)} TL fiyatından gerçekleşti.</p>
                <p><strong>İşlem Numarası:</strong> ${txn}</p>
                <p><strong>Toplam:</strong> ${formatPrice(total)} TL</p>
                <p><strong>Komisyon:</strong> ${formatPrice(commission)} TL</p>
                <p><strong>Net:</strong> ${formatPrice(net)} TL</p>
              `;
            } else if (statusStr === 'OPEN' || category === 'LIMIT') {
              subject = `Limit ${type === 'BUY' ? 'Alım' : 'Satım'} Emri Alındı`;
              content = `
                <h2>${subject}</h2>
                <p>${stock.code} için ${quantity} adet ${type === 'BUY' ? 'alım' : 'satım'} limit emriniz oluşturuldu ve beklemede.</p>
                <p><strong>Limit Fiyat:</strong> ${formatPrice(unitPrice)} TL</p>
                <p><strong>Emir Durumu:</strong> ${statusStr || 'OPEN'}</p>
                <p><strong>Emir Numarası:</strong> ${txn}</p>
              `;
            } else {
              subject = `Emir Durumu: ${statusStr || 'Bilinmiyor'}`;
              content = `
                <h2>${subject}</h2>
                <p>${stock.code} için verdiğiniz emir durumu: ${statusStr || 'Bilinmiyor'}.</p>
                <p><strong>Emir Numarası:</strong> ${txn}</p>
              `;
            }

            if (to) {
              await sendMail({ to, subject, content, recipientName });
            }
          } catch {}

          if (type === 'BUY') {
            await fetchBalance();
            await fetchSellableQty();
          } else {
            if (statusStr === 'FILLED' || category === 'MARKET' || statusStr) {
              setSellHold((h) => normalizeNumber(h) + Number(quantity));
            }
            await fetchSellableQty();
          }

          setIsSubmitting(false);
          openModal({
            variant: 'success',
            title: 'İşlem Başarılı',
            message: `${type === 'BUY' ? 'Alış' : 'Satış'} emri gönderildi.`,
            confirmText: 'Tamam',
            onClose: () => {
              closeModal();
            }
          });
        } catch (err) {
          setIsSubmitting(false);
          openModal({
            variant: 'error',
            title: 'İşlem Başarısız',
            message: `Emir gönderilemedi.\n${err?.message || err}`,
            confirmText: 'Tamam',
            onClose: closeModal
          });
        }
      },
      onClose: closeModal
    });
  };

  const sellDisabled = type === 'SELL' && effectiveSellable <= 0;

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
        {type === 'BUY' && (
          <div className={styles.balanceInfo}>
            <strong>Mevcut Bakiye:</strong> {availableBalance !== null ? `${formatPrice(availableBalance)} TL` : 'Yükleniyor...'}
          </div>
        )}

        {type === 'SELL' && (
          <div className={styles.balanceInfo}>
            <strong>Mevcut Hisse Adedi:</strong> {displaySellable !== null ? displaySellable : 'Yükleniyor...'}
          </div>
        )}

        <div className={styles.field}>
          <label>Hisse Adedi</label>
          <input
            type="number"
            min="0"
            value={quantity}
            disabled={isSubmitting}
            onChange={e => {
              const val = Number(e.target.value);
              if (val < 0) return;
              setQuantity(e.target.value);
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
            disabled={isSubmitting}
            onChange={e => {
              const value = parseFloat(e.target.value);
              if (value < 0) return;
              setTotalInput(e.target.value);
            }}
            placeholder="Toplam Tutar"
          />
        </div>

        <div className={styles.inlineGroup}>
          <div className={styles.field}>
            <label>Emir Türü</label>
            <select
              value={category}
              disabled={isSubmitting}
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
                disabled={isSubmitting}
                onChange={e => setPrice(e.target.value)}
                placeholder="TL"
              />
            </div>
          )}
        </div>

        <div className={styles.summary}>
          <div><span>Birim Fiyat:</span><span>{formatPrice(unitPrice)}</span></div>
          <div><span>Adet:</span><span>{quantity || '-'}</span></div>
          <div><span>Toplam Tutar:</span><span>{total} TL</span></div>
          <div><span>Komisyon (%{(commissionRate * 100).toFixed(2)}):</span><span>{commission} TL</span></div>
          <div><span>Net Tutar:</span><span>{net} TL</span></div>
        </div>

        <button
          className={styles.actionBtn}
          onClick={handleSubmit}
          disabled={isSubmitting}
          title=""
        >
          {type === 'BUY' ? 'Alış Emri Ver' : 'Satış Emri Ver'}
        </button>
      </div>

      {modalConfig && <Modal {...modalConfig} />}
    </div>
  );
};

export default TradePanel;
