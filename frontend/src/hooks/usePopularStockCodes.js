import { useState, useEffect } from 'react';
import { orderApi } from '../server/order';

export default function usePopularStockCodes() {
  const [codes, setCodes] = useState([]);

  useEffect(() => {
    let mounted = true;

    orderApi.getPopularStockCodes()
      .then(body => {
        console.log('[usePopularStockCodes] API cevabı:', body);
        // body.data → Array(5)
        if (mounted && Array.isArray(body.data)) {
          const onlyCodes = body.data.map(item => item.stockCode);
          console.log('[usePopularStockCodes] Bulunan kodlar:', onlyCodes);
          setCodes(onlyCodes);
        } else {
          console.warn('[usePopularStockCodes] data bir dizi değil:', body.data);
        }
      })
      .catch(err => {
        console.error('[usePopularStockCodes] Hata:', err);
      });

    return () => { mounted = false; };
  }, []);

  return codes;
}