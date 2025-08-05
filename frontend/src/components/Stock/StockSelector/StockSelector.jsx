import React, { useState, useMemo } from 'react';
import SearchBar from '../SearchBar/SearchBar';
import StockList from '../StockList/StockList';
import useStockPrices from '../../../hooks/useStockPrices';
import styles from './StockSelector.module.css';

const StockSelector = ({ onStockClick }) => {
  const stocks = useStockPrices();
  const [query, setQuery] = useState('');
  const [selected, setSelected] = useState(null);

  const filtered = useMemo(
    () =>
      stocks.filter(
        s =>
          s.code.toLowerCase().includes(query.toLowerCase()) ||
          s.text.toLowerCase().includes(query.toLowerCase())
      ),
    [stocks, query]
  );

  const handleSelect = stock => {
    setSelected(stock.code);
    if (onStockClick) onStockClick(stock);
  };

  return (
     <div className={styles.container}>
        <SearchBar value={query} onChange={setQuery} placeholder="Kod veya şirket adı girin" />
      <div className={styles.headerRow}>
        <span className={styles.colHeader}>Hisse</span>
        <span className={styles.colHeader}>Fiyat</span>
        <span className={styles.colHeader}>Değişim</span>
        <span className={styles.colHeader}>Gün Y/D</span>
        <span className={styles.colHeaderRight}>Hacim</span>
      </div>
      
      <StockList stocks={filtered} onSelect={handleSelect} selectedCode={selected} />
    </div>
  );
};

export default StockSelector;
