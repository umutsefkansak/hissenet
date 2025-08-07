import { useState, useEffect } from 'react';
import stockService from '../server/stockService';

export default function useStockPrices() {
    const [stocks, setStocks] = useState([]);

    /*useEffect(() => {
        stockService.connect(setStocks);
        return () => {
            stockService.disconnect();
        };
    }, []);
*/
    useEffect(() => {
        stockService.subscribe('/topic/prices', setStocks);
        return () => stockService.unsubscribe('/topic/prices');
    }, []);


    return stocks;
}
