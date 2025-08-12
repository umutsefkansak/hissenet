import React from 'react';
import styles from './PopularStockItem.module.css';
import { formatPrice, formatChange, formatRate, isPositive } from '../../utils/formatters';
import UpArrow from '../Icons/UpArrow';
import DownArrow from '../Icons/DownArrow';

export default function PopularStockItem({ stock }) {

if (!stock) {
    return (
      <tr className={styles.row}>
        <td className={styles.code}>—</td>
        <td className={styles.price}>—</td>
        <td className={styles.change}>—</td>
        <td className={styles.rate}>—</td>
      </tr>
    );
  }

 const {
    code = '—',
    lastPrice = null,
    changePrice = null,
    rate = null,
    icon = '',
  } = stock;

  const positive = isPositive(changePrice);
  const iconSrc = icon && typeof icon === 'string' ? icon : '/icons/placeholder.svg';

  return (
    <tr className={styles.row}>
      <td className={styles.code}>
        <img
          src={iconSrc}
          alt={code}
          className={styles.logo}
          width={20}
          height={20}
          loading="lazy"
          onError={(e) => { e.currentTarget.src = '/icons/placeholder.svg'; }}
        />
        {code}
      </td>
      <td className={styles.price}>{formatPrice(lastPrice)}</td>
      <td className={`${styles.change} ${positive ? styles.positive : styles.negative}`}>
        {positive
          ? <UpArrow className={styles.arrowIcon} />
          : <DownArrow className={styles.arrowIcon} />
        }
        <span className={styles.value}>{formatChange(changePrice)}</span>
      </td>
      <td className={`${styles.rate} ${positive ? styles.positive : styles.negative}`}>
        {formatRate(rate)}
      </td>
    </tr>
  );
}