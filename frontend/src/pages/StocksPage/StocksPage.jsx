import React, { useState } from 'react';
import StockSelector from '../../components/Stock/StockSelector/StockSelector';
import './StocksPage.css';
import TradePanel from '../../components/TradePanel/TradePanel'

const StocksPage = () => {
    const [selectedStock, setSelectedStock] = useState(null);

    return (
        <div className="stocks-container">
            {/* Sol panel */}
            <div className={selectedStock ? 'stocks-left half' : 'stocks-left full'}>
                <StockSelector onStockClick={setSelectedStock} />
            </div>
            {/* Sağ panel yalnızca bir hisse seçildiğinde görünür */}
            {selectedStock && (
                <div className="stocks-right half">
                    <TradePanel
                        stock={selectedStock}
                        onBack={() => setSelectedStock(null)}
                    />
                </div>
            )}
        </div>
        
    );
};

export default StocksPage;
