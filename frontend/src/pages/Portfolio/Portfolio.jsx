import React, { useState } from 'react';
import './Portfolio.css';

const Portfolio = () => {
  const [selectedPortfolio, setSelectedPortfolio] = useState('portfolio1');

  // Mock data for multiple portfolios
  const portfolios = {
    portfolio1: {
      name: 'Ana Portföy',
      totalValue: 375000,
      availableBalance: 125000,
      portfolioValue: 250000,
      blockedBalance: 0,
      totalProfit: 25000,
      profitPercentage: 7.14,
      stocks: [
        { symbol: 'THYAO', name: 'Türk Hava Yolları', quantity: 100, avgPrice: 200.00, currentPrice: 250.50, profit: 5050, profitPercentage: 25.25, currentValue: 25050 },
        { symbol: 'BIST100', name: 'BIST 100 Endeksi', quantity: 50, avgPrice: 9500, currentPrice: 9845, profit: 17250, profitPercentage: 3.63, currentValue: 492250 },
        { symbol: 'ARCLK', name: 'Arçelik', quantity: 200, avgPrice: 16.00, currentPrice: 15.21, profit: -158, profitPercentage: -4.94, currentValue: 3042 },
        { symbol: 'GARAN', name: 'Garanti Bankası', quantity: 150, avgPrice: 40.00, currentPrice: 42.88, profit: 432, profitPercentage: 7.20, currentValue: 6432 },
        { symbol: 'AKBNK', name: 'Akbank', quantity: 100, avgPrice: 35.00, currentPrice: 38.50, profit: 350, profitPercentage: 10.00, currentValue: 3850 }
      ]
    },
    portfolio2: {
      name: 'Emeklilik Portföyü',
      totalValue: 180000,
      availableBalance: 45000,
      portfolioValue: 135000,
      blockedBalance: 0,
      totalProfit: 12000,
      profitPercentage: 7.14,
      stocks: [
        { symbol: 'GARAN', name: 'Garanti Bankası', quantity: 200, avgPrice: 38.00, currentPrice: 42.88, profit: 976, profitPercentage: 12.84, currentValue: 8576 },
        { symbol: 'AKBNK', name: 'Akbank', quantity: 150, avgPrice: 32.00, currentPrice: 38.50, profit: 975, profitPercentage: 20.31, currentValue: 5775 },
        { symbol: 'THYAO', name: 'Türk Hava Yolları', quantity: 50, avgPrice: 220.00, currentPrice: 250.50, profit: 1525, profitPercentage: 13.86, currentValue: 12525 },
        { symbol: 'ASELS', name: 'Aselsan', quantity: 300, avgPrice: 12.00, currentPrice: 11.80, profit: -60, profitPercentage: -5.00, currentValue: 3540 }
      ]
    },
    portfolio3: {
      name: 'Yatırım Portföyü',
      totalValue: 520000,
      availableBalance: 80000,
      portfolioValue: 440000,
      blockedBalance: 0,
      totalProfit: 35000,
      profitPercentage: 7.22,
      stocks: [
        { symbol: 'BIST100', name: 'BIST 100 Endeksi', quantity: 100, avgPrice: 9200, currentPrice: 9845, profit: 64500, profitPercentage: 7.01, currentValue: 984500 },
        { symbol: 'THYAO', name: 'Türk Hava Yolları', quantity: 200, avgPrice: 180.00, currentPrice: 250.50, profit: 14100, profitPercentage: 39.17, currentValue: 50100 },
        { symbol: 'GARAN', name: 'Garanti Bankası', quantity: 300, avgPrice: 35.00, currentPrice: 42.88, profit: 2364, profitPercentage: 22.51, currentValue: 12864 },
        { symbol: 'ARCLK', name: 'Arçelik', quantity: 500, avgPrice: 14.00, currentPrice: 15.21, profit: 605, profitPercentage: 8.64, currentValue: 7605 },
        { symbol: 'KRDMD', name: 'Kardemir', quantity: 400, avgPrice: 8.50, currentPrice: 9.45, profit: 380, profitPercentage: 11.18, currentValue: 3780 }
      ]
    }
  };

  const portfolioData = portfolios[selectedPortfolio];

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(amount);
  };

  const formatPercentage = (percentage) => {
    return `${percentage > 0 ? '+' : ''}${percentage.toFixed(2)}%`;
  };

  return (
    <div className="portfolio">
      {/* Header Section */}
      <div className="portfolio-header">
        <div className="header-left">
          <h2>Hoş Geldiniz</h2>
          <p>Güncel piyasa durum ve platform özeti</p>
          <h1>Müşteri Portföyü</h1>
        </div>
        <div className="header-right">
          <div className="portfolio-selector">
            <select 
              value={selectedPortfolio} 
              onChange={(e) => setSelectedPortfolio(e.target.value)}
              className="portfolio-dropdown"
            >
              {Object.keys(portfolios).map((key) => (
                <option key={key} value={key}>
                  {portfolios[key].name}
                </option>
              ))}
            </select>
          </div>
          <button className="cash-transaction-btn">Nakit İşlemler</button>
        </div>
      </div>

      {/* Portfolio Summary Cards */}
      <div className="portfolio-summary">
        <div className="summary-card total-value">
          <div className="card-content">
            <h3>Toplam Değer</h3>
            <p className="card-amount">{formatCurrency(portfolioData.totalValue)}</p>
          </div>
        </div>
        
        <div className="summary-card available-balance">
          <div className="card-content">
            <h3>Kullanılabilir Bakiye</h3>
            <p className="card-amount">{formatCurrency(portfolioData.availableBalance)}</p>
          </div>
        </div>
        
        <div className="summary-card portfolio-value">
          <div className="card-content">
            <h3>Portföy Değeri</h3>
            <p className="card-amount">{formatCurrency(portfolioData.portfolioValue)}</p>
          </div>
        </div>
        
        <div className="summary-card blocked-balance">
          <div className="card-content">
            <h3>Bloke Bakiye</h3>
            <p className="card-amount">{formatCurrency(portfolioData.blockedBalance)}</p>
          </div>
        </div>
        
        <div className="summary-card total-profit">
          <div className="card-content">
            <h3>Toplam Kar/Zarar</h3>
            <p className={`card-amount ${portfolioData.totalProfit >= 0 ? 'positive' : 'negative'}`}>
              {formatCurrency(portfolioData.totalProfit)}
            </p>
          </div>
        </div>
      </div>

      {/* Main Content Sections */}
      <div className="portfolio-main-content">
        {/* Left Section: Portfolio Distribution */}
        <div className="portfolio-section">
          <h3>Portföy Dağılımı</h3>
          <div className="chart-container">
            <div className="donut-chart">
              <div className="donut-segment" style={{ 
                background: 'conic-gradient(#10b981 0deg 180deg, #3b82f6 180deg 220deg, #8b5cf6 220deg 250deg, #f59e0b 250deg 280deg, #ef4444 280deg 360deg)' 
              }}></div>
            </div>
            <div className="chart-legend">
              {portfolioData.stocks.map((stock, index) => {
                const colors = ['#10b981', '#3b82f6', '#8b5cf6', '#f59e0b', '#ef4444'];
                return (
                  <div key={index} className="legend-item">
                    <span className="legend-color" style={{ backgroundColor: colors[index % colors.length] }}></span>
                    <span>{stock.symbol}</span>
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        {/* Right Section: Portfolio Content */}
        <div className="portfolio-section">
          <h3>Portföy İçeriği</h3>
          <div className="portfolio-table">
            <table>
              <thead>
                <tr>
                  <th>Hisse</th>
                  <th>Güncel Fiyat</th>
                  <th>Ort. Alış Fiyatı</th>
                  <th>Kar/Zarar</th>
                  <th>Adet</th>
                  <th>Güncel Değer</th>
                </tr>
              </thead>
              <tbody>
                {portfolioData.stocks.map((stock, index) => (
                  <tr key={index}>
                    <td>
                      <div className="stock-info">
                        <span className="stock-symbol">{stock.symbol}</span>
                      </div>
                    </td>
                    <td>{formatCurrency(stock.currentPrice)}</td>
                    <td>{formatCurrency(stock.avgPrice)}</td>
                    <td>
                      <div className="profit-info">
                        <span className={`profit-amount ${stock.profit >= 0 ? 'positive' : 'negative'}`}>
                          {formatCurrency(stock.profit)}
                        </span>
                        <span className={`profit-percentage ${stock.profitPercentage >= 0 ? 'positive' : 'negative'}`}>
                          {formatPercentage(stock.profitPercentage)}
                        </span>
                      </div>
                    </td>
                    <td>{stock.quantity.toLocaleString()}</td>
                    <td>{formatCurrency(stock.currentValue)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Portfolio; 