import React, { useState, useEffect } from 'react';
import styles from './TradePanel.module.css';
import { formatPrice } from '../../utils/formatters';
import { orderApi } from '../../server/order';

const TradePanel = ({ stock, onBack }) => {
    const [type, setType] = useState("BUY"); // BUY | SELL
    const [category, setCategory] = useState("MARKET"); // MARKET | LIMIT
    const [quantity, setQuantity] = useState(''); // Adet
    const [totalInput, setTotalInput] = useState('');
    const [price, setPrice] = useState(''); // Limit fiyat

    const customerId = 68; // Şimdilik sabit

    useEffect(() => {
        setPrice(stock.lastPrice?.toFixed(2) || '');
    }, [stock]);

    const unitPrice = category === 'LIMIT' ? parseFloat(price) || 0 : stock.lastPrice || 0;

    useEffect(() => {
        if (totalInput && unitPrice > 0) {
            const maxShares = Math.floor(parseFloat(totalInput) / unitPrice);
            setQuantity(maxShares.toString());
        }
    }, [totalInput, unitPrice]);

    const total = quantity && unitPrice ? (unitPrice * Number(quantity)).toFixed(2) : '0.00';
    const commission = (total * 0.005).toFixed(2);
    const net = (total - commission).toFixed(2);

    const handleSubmit = async () => {
        if (!customerId || !stock?.code || !quantity || Number(quantity) <= 0) {
            alert("Lütfen tüm alanları doğru doldurun.");
            return;
        }

        const payload = {
            customerId: Number(customerId),
            stockCode: stock.code,
            quantity: Number(quantity),
            price: category === "MARKET" ? stock.lastPrice : Number(price),
            type: type,
            category: category
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
            {/* Üst Bar */}
            <div className={styles.topBar}>
                <button className={styles.backBtn} onClick={onBack}>&larr;</button>
                <h3 className={styles.title}>{stock.code} - {stock.text}</h3>
                <div className={styles.currentPrice}>{formatPrice(stock.lastPrice)}</div>
            </div>

            {/* Sekmeler */}
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

            {/* Emir Kartı */}
            <div className={styles.card}>
                <h3 className={styles.cardTitle}>
                    {type === 'BUY' ? 'Alış Emri' : 'Satış Emri'}
                </h3>

                <div className={styles.field}>
                    <label>Hisse Adedi (quantity)</label>
                    <input
                        type="number"
                        value={quantity}
                        onChange={e => setQuantity(e.target.value)}
                        placeholder="Adet"
                    />
                </div>

                <div className={styles.field}>
                    <label>Toplam Tutar (TL)</label>
                    <input
                        type="number"
                        value={totalInput}
                        onChange={e => setTotalInput(e.target.value)}
                        placeholder="Toplam Tutar"
                    />
                </div>

                <div className={styles.field}>
                    <label>Emir Türü (category)</label>
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
                        <label>Birim Fiyat (price)</label>
                        <input
                            type="number"
                            value={price}
                            onChange={e => setPrice(e.target.value)}
                            placeholder="TL"
                        />
                    </div>
                )}

                {/* Özet */}
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
