import React from 'react';
import StockItem from '../StockItem/StockItem';
import styles from './StockList.module.css';

const StockList = ({ stocks, onSelect, selectedCode }) => (
  <div className={styles.list}>
    {stocks.map(s => (
      <StockItem
        key={s.code}
        stock={s}
        onSelect={onSelect}
        isSelected={s.code === selectedCode}
      />
    ))}
  </div>
);

export default StockList;
