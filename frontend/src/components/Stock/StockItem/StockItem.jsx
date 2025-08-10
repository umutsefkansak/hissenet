import styles from './StockItem.module.css';
import { formatPrice, formatChange, formatRate, isPositive, formatHacim } from '../../../utils/formatters';
import UpArrow from '../../Icons/UpArrow';
import DownArrow from '../../Icons/DownArrow';
import LogoPlaceholder from '../../Icons/LogoPlaceholder';
import { useState } from 'react';

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
    icon = '',
  } = stock || {};

  const positive = isPositive(changePrice);
  const [logoError, setLogoError] = useState(false);

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
        <div className={styles.nameWithLogo}>
           {logoError || !icon ? (
            <LogoPlaceholder className={styles.logo} />
          ) : (
            <img
              src={icon}
              alt={code}
              className={styles.logo}
              loading="lazy"
              onError={() => setLogoError(true)}
            />
          )}
          <div className={styles.nameText}>
            <span className={styles.code}>{code}</span>
            <span className={styles.textRow}>{text}</span>
          </div>
        </div>
      </div>

      {/* Column: Fiyat */}
      <div className={styles.column}>
        <div className={styles.priceRow}>{formatPrice(lastPrice)}</div>
        <div className={styles.subText}>Açılış: {formatPrice(openPrice)}</div>
      </div>

      {/* Column: Değişim */}
      <div className={styles.changeColumnContainer}>
        <div
          className={`${styles.changeRow} ${positive ? styles.positive : styles.negative
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
