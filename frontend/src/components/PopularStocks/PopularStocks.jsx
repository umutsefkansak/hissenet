import React from 'react';
import styles from './PopularStocks.module.css';
import usePopularStockCodes from '../../hooks/usePopularStockCodes';
import useStockPrices from '../../hooks/useStockPrices';
import PopularStockItem from './PopularStockItem';

export default function PopularStocks({ className }) {
  const codes = usePopularStockCodes();
  const stocks = useStockPrices();

   const isLoading = stocks.length === 0;

  return (
    <div className={`${styles.container} ${className || ''}`}>
      <h3 className={styles.title}>Popüler Hisseler</h3>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>Hisseler</th>
            <th>Fiyat</th>
            <th>Değişim</th>
            <th>Oran</th>
          </tr>
        </thead>
        <tbody>
           {isLoading && (
            <tr>
              <td colSpan={4} className={styles.loading}>
                Yükleniyor…
              </td>
            </tr>
          )}

          {!isLoading && codes.map(code => {
            // WS verisinden kodu bul
            const stock = stocks.find(s => s.code === code);
            // eğer o kod için henüz veri yoksa atla
            if (!stock) return null;
            return <PopularStockItem key={code} stock={stock} />;
          })}

          {/* Kodlar gelmiş ama hiç eşleşen stock yoksa */}
          {!isLoading && codes.length > 0 && stocks.every(s => !codes.includes(s.code)) && (
            <tr>
              <td colSpan={4} className={styles.loading}>
                Popüler hisselere ait veri bulunamadı
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}