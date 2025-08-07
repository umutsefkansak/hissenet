import React from 'react';
import styles from './PopularStockItem.module.css';
import { formatPrice, formatChange, formatRate, isPositive } from '../../utils/formatters';
import UpArrow from '../Icons/UpArrow';
import DownArrow from '../Icons/DownArrow';

export default function PopularStockItem({ stock }) {

 const {
    code = '—',
    lastPrice = null,
    changePrice = null,
    rate = null,
  } = stock || {};

  const positive = isPositive(changePrice);

  return (
    <tr className={styles.row}>
      <td className={styles.code}>{code}</td>
      <td className={styles.price}>{formatPrice(lastPrice)}</td>
      <td className={`${styles.change} ${positive ? styles.positive : styles.negative}`}>
        {positive
          ? <UpArrow className={styles.icon} />
          : <DownArrow className={styles.icon} />
        }
        <span className={styles.value}>{formatChange(changePrice)}</span>
      </td>
      <td className={`${styles.rate} ${positive ? styles.positive : styles.negative}`}>
        {formatRate(rate)}
      </td>
    </tr>
  );
}