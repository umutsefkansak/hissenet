import { useState, useEffect } from 'react';
import stockService from '../server/websocket/stock';

export default function useStockPrices() {
    const [stocks, setStocks] = useState([]);

    useEffect(() => {
        stockService.subscribe('/topic/prices', setStocks);
        return () => stockService.unsubscribe('/topic/prices');
    }, []);


    return stocks;
}
