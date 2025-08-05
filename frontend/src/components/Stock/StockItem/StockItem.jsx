import React from 'react';
import styles from './StockItem.module.css';
import { formatPrice, formatChange, formatRate, isPositive, formatHacim } from '../../../utils/formatters';

const StockItem = ({ stock, onSelect, isSelected }) => {
 const {
    code = '—',
    text = '—',
    lastPrice = null,
    openPrice = null,
    changePrice = null,
    rate = null,
    max = null,
    min = null,
    hacim = null,
  } = stock || {};
    
  const positive = isPositive(changePrice);

  const handleClick = () => onSelect && onSelect(stock);


  return (
   <div
       role="button"
      tabIndex={0}
      className={`${styles.item} ${isSelected ? styles.selected : ''}`}
      onClick={handleClick}
      onKeyPress={e => e.key === 'Enter' && handleClick()}
    >
      {/* Column: Hisse */}
      <div className={styles.column}>
        <div className={styles.nameRow}>
          <span className={styles.code}>{code}</span>
        </div>
        <div className={styles.textRow}>{text}</div>
      </div>

      {/* Column: Fiyat */}
      <div className={styles.column}>
        <div className={styles.priceRow}>{formatPrice(lastPrice)}</div>
        <div className={styles.subText}>Açılış: {formatPrice(openPrice)}</div>
      </div>

      {/* Column: Değişim */}
      <div className={styles.column}>
        <div
          className={`${styles.changeRow} ${
            positive ? styles.positive : styles.negative
          }`}
        >
          {positive ? '↑' : '↓'} {formatRate(rate)}
        </div>
        <div className={styles.subText}>
          {positive ? '' : ''}{formatChange(changePrice)} TL
        </div>
      </div>

       {/* Column: Gün Yüksek/Düşük (Max/Min) */}
      <div className={styles.column}>
        <div className={styles.subText}>Y: {formatPrice(max)}</div>
        <div className={styles.subText}>D: {formatPrice(min)}</div>
      </div>

      {/* Column: Hacim */}
      <div className={styles.columnRight}>
        <div className={styles.subText}>{formatHacim(hacim)}</div>
      </div>
    </div>
  );
};

export default StockItem;
