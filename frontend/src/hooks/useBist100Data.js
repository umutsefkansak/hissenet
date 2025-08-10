import { useState, useEffect } from 'react';
import stockService from '../server/websocket/stock';

export default function useBist100Data() {
  const [bist, setBist] = useState(null);

  useEffect(() => {
    console.log('HOOK[BIST] başlatıldı → /topic/bist100 abonesi olunuyor');

    const onMsg = (data) => {
      const boyut = (JSON.stringify(data) || '').length;
      console.log('HOOK[BIST] mesaj alındı boyut=%d karakter veri=%o', boyut, data);
      setBist(data);
    };

    stockService.subscribe('/topic/bist100', onMsg);

    return () => {
      console.log('HOOK[BIST] sonlandırıldı → /topic/bist100 aboneliği iptal ediliyor');
      stockService.unsubscribe('/topic/bist100');
    };
  }, []);

  return bist;
}
