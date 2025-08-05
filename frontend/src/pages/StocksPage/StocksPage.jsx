import React, { useState } from 'react';
import StockSelector from '../../components/Stock/StockSelector/StockSelector';
import './StocksPage.css';

const StocksPage = () => {
    const [selectedStock, setSelectedStock] = useState(null);

    return (
        <div className="stocks-container">
            {/* Sol panel */}
            <div className={selectedStock ? 'stocks-left half' : 'stocks-left full'}>
                <StockSelector onStockClick={setSelectedStock} />
            </div>
        </div>
    );
};

export default StocksPage;
