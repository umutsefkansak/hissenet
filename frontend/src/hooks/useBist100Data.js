// src/hooks/useBist100Data.js
import { useState, useEffect } from 'react';
import stockService from '../server/stockService';

export default function useBist100Data() {
  const [bist, setBist] = useState(null);

  useEffect(() => {
    stockService.subscribe('/topic/bist100', setBist);
    return () => stockService.unsubscribe('/topic/bist100');
  }, []);

  return bist;
}
