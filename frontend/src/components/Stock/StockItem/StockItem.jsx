import React from 'react';
import styles from './StockItem.module.css';
import { formatPrice, formatChange, formatRate, isPositive, formatHacim } from '../../../utils/formatters';
import UpArrow from '../../Icons/UpArrow';
import DownArrow from '../../Icons/DownArrow';

const StockItem = ({ stock, onSelect, isSelected }) => {
 const {
    code = '—',
    text = '—',
    lastPrice = null,
    openPrice = null,
    changePrice = null,
    previousClosePrice = null,
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
        <div className={styles.subText}>Önceki Kapanış: {formatPrice(previousClosePrice)}</div>
      </div>

      {/* Column: Değişim */}
      <div className={styles.changeColumnContainer}>
        <div
          className={`${styles.changeRow} ${
            positive ? styles.positive : styles.negative
          }`}
        >
          {positive
          ? <UpArrow className={styles.icon} />
          : <DownArrow className={styles.icon} />
        } 
         <div className={styles.changeColumn}>
            <span className={styles.rateValue}>
              {formatRate(rate)}
            </span>
            <span className={styles.changeValue}>
              {formatChange(changePrice)} TL
            </span>
          </div>
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
