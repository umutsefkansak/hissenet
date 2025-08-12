import { useState, useEffect, useRef } from 'react';
import stockService from '../server/websocket/stock';

export default function useStockPrices() {
  const [stocks, setStocks] = useState([]);
  const toplamMesaj = useRef(0);

  useEffect(() => {
    console.log('HOOK[PRICES] başlatıldı → /topic/prices abonesi olunuyor');

    const onMsg = (data) => {
      const diziMi = Array.isArray(data);
      const uzunluk = diziMi ? data.length : -1;
      const boyut = (JSON.stringify(data) || '').length;
      toplamMesaj.current += 1;

      console.log(
        'HOOK[PRICES] mesaj #%d dizi=%s uzunluk=%d boyut=%d karakter',
        toplamMesaj.current,
        diziMi,
        uzunluk,
        boyut
      );

      if (!diziMi) {
        console.warn('HOOK[PRICES] gelen veri dizi değil → %o', data);
        setStocks([]);
        return;
      }

      setStocks(data);
    };

    stockService.subscribe('/topic/prices', onMsg);

    return () => {
      console.log('HOOK[PRICES] sonlandırıldı → /topic/prices aboneliği iptal ediliyor');
      stockService.unsubscribe('/topic/prices');
    };
  }, []);

  return stocks;
}
