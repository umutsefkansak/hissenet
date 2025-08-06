import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { portfolioApi } from '../../services/api/portfolioApi';
import { walletApi } from '../../services/api/walletApi';
import './Portfolio.css';

const Portfolio = () => {
  const { customerId = '68' } = useParams(); // Default to 68 for testing
  const [portfolios, setPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [stockTransactions, setStockTransactions] = useState([]);
  const [walletBalance, setWalletBalance] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch portfolios and wallet balance on component mount
  useEffect(() => {
    fetchPortfolios();
    fetchWalletBalance();
  }, [customerId]);

  // Fetch stock transactions when selected portfolio changes
  useEffect(() => {
    if (selectedPortfolio) {
      fetchStockTransactions(selectedPortfolio.id);
    }
  }, [selectedPortfolio]);

  const fetchPortfolios = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await portfolioApi.getCustomerPortfolios(customerId);
      
      if (response.status === 200 && response.data) {
        setPortfolios(response.data);
        // Set first portfolio as selected by default
        if (response.data.length > 0) {
          setSelectedPortfolio(response.data[0]);
        }
      }
    } catch (err) {
      console.error('Error fetching portfolios:', err);
      setError('Portföy bilgileri yüklenirken hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const fetchWalletBalance = async () => {
    try {
      const response = await walletApi.getCustomerWalletBalance(customerId);
      
      if (response.status === 200 && response.data !== undefined) {
        setWalletBalance(response.data);
      }
    } catch (err) {
      console.error('Error fetching wallet balance:', err);
      setWalletBalance(0);
    }
  };

  const fetchStockTransactions = async (portfolioId) => {
    try {
      const response = await portfolioApi.getPortfolioStockTransactions(portfolioId);
      
      if (response.status === 200 && response.data) {
        setStockTransactions(response.data);
      }
    } catch (err) {
      console.error('Error fetching stock transactions:', err);
      setStockTransactions([]);
    }
  };

  // Calculate total portfolio values
  const calculatePortfolioTotals = (portfolio) => {
    if (!portfolio) return {};

    return {
      totalValue: portfolio.totalValue || 0,
      totalProfit: portfolio.totalProfitLoss || 0,
      profitPercentage: portfolio.profitLossPercentage || 0,
      availableBalance: walletBalance,
      blockedBalance: 0 // This should come from another API endpoint
    };
  };

  // Calculate totals for all portfolios
  const calculateAllPortfoliosTotal = () => {
    if (!portfolios || portfolios.length === 0) return 0;
    return portfolios.reduce((sum, portfolio) => sum + (portfolio.totalValue || 0), 0);
  };

  // Calculate stock distribution for pie chart
  const calculateStockDistribution = () => {
    if (!stockTransactions || stockTransactions.length === 0) return [];

    // Group transactions by stock code and sum quantities
    const stockGroups = stockTransactions.reduce((groups, transaction) => {
      const stockCode = transaction.stockCode;
      if (!groups[stockCode]) {
        groups[stockCode] = {
          stockCode: stockCode,
          totalQuantity: 0,
          totalValue: 0
        };
      }
      groups[stockCode].totalQuantity += transaction.quantity;
      groups[stockCode].totalValue += transaction.quantity * transaction.currentPrice;
      return groups;
    }, {});

    // Convert to array and sort by quantity
    const stockArray = Object.values(stockGroups).sort((a, b) => b.totalQuantity - a.totalQuantity);
    
    // Calculate total quantity for percentages
    const totalQuantity = stockArray.reduce((sum, stock) => sum + stock.totalQuantity, 0);
    
    // Calculate percentages and degrees for pie chart
    let currentDegree = 0;
    const colors = ['#10b981', '#3b82f6', '#8b5cf6', '#f59e0b', '#ef4444', '#06b6d4', '#84cc16', '#f97316'];
    
    return stockArray.map((stock, index) => {
      const percentage = totalQuantity > 0 ? (stock.totalQuantity / totalQuantity) * 100 : 0;
      const degree = (percentage / 100) * 360;
      const startDegree = currentDegree;
      currentDegree += degree;
      
      return {
        ...stock,
        percentage: percentage,
        startDegree: startDegree,
        endDegree: currentDegree,
        color: colors[index % colors.length]
      };
    });
  };

  // Generate pie chart CSS
  const generatePieChartCSS = (stockDistribution) => {
    if (stockDistribution.length === 0) return '';

    const segments = stockDistribution.map(stock => 
      `${stock.color} ${stock.startDegree}deg ${stock.endDegree}deg`
    ).join(', ');

    return `conic-gradient(${segments})`;
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(amount);
  };

  const formatPercentage = (percentage) => {
    return `${percentage > 0 ? '+' : ''}${percentage.toFixed(2)}%`;
  };

  if (loading) {
    return (
      <div className="portfolio">
        <div className="loading">Portföy bilgileri yükleniyor...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="portfolio">
        <div className="error">{error}</div>
        <button onClick={fetchPortfolios} className="retry-btn">Tekrar Dene</button>
      </div>
    );
  }

  if (!selectedPortfolio) {
    return (
      <div className="portfolio">
        <div className="no-portfolio">Portföy bulunamadı</div>
      </div>
    );
  }

  const portfolioTotals = calculatePortfolioTotals(selectedPortfolio);
  const stockDistribution = calculateStockDistribution();

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
              value={selectedPortfolio.id} 
              onChange={(e) => {
                const portfolio = portfolios.find(p => p.id === parseInt(e.target.value));
                setSelectedPortfolio(portfolio);
              }}
              className="portfolio-dropdown"
            >
              {portfolios.map((portfolio) => (
                <option key={portfolio.id} value={portfolio.id}>
                  {portfolio.portfolioName}
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
            <p className="card-amount">{formatCurrency(calculateAllPortfoliosTotal())}</p>
          </div>
        </div>
        
        <div className="summary-card available-balance">
          <div className="card-content">
            <h3>Kullanılabilir Bakiye</h3>
            <p className="card-amount">{formatCurrency(portfolioTotals.availableBalance)}</p>
          </div>
        </div>
        
        <div className="summary-card portfolio-value">
          <div className="card-content">
            <h3>Portföy Değeri</h3>
            <p className="card-amount">{formatCurrency(portfolioTotals.totalValue)}</p>
          </div>
        </div>
        
        <div className="summary-card blocked-balance">
          <div className="card-content">
            <h3>Bloke Bakiye</h3>
            <p className="card-amount">{formatCurrency(portfolioTotals.blockedBalance)}</p>
          </div>
        </div>
        
        <div className="summary-card total-profit">
          <div className="card-content">
            <h3>Toplam Kar/Zarar</h3>
            <p className={`card-amount ${portfolioTotals.totalProfit >= 0 ? 'positive' : 'negative'}`}>
              {formatCurrency(portfolioTotals.totalProfit)}
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
                background: generatePieChartCSS(stockDistribution)
              }}></div>
            </div>
            <div className="chart-legend">
              {stockDistribution.map((stock, index) => (
                <div key={index} className="legend-item">
                  <span className="legend-color" style={{ backgroundColor: stock.color }}></span>
                  <span>{stock.stockCode} ({stock.percentage.toFixed(1)}%)</span>
                </div>
              ))}
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
                  <th>Alış Fiyatı</th>
                  <th>Kar/Zarar</th>
                  <th>Adet</th>
                  <th>Güncel Değer</th>
                </tr>
              </thead>
              <tbody>
                {stockTransactions.map((transaction, index) => {
                  const currentValue = transaction.quantity * transaction.currentPrice;
                  const profit = currentValue - transaction.totalAmount;
                  const profitPercentage = transaction.totalAmount > 0 ? (profit / transaction.totalAmount) * 100 : 0;
                  
                  return (
                    <tr key={index}>
                      <td>
                        <div className="stock-info">
                          <span className="stock-symbol">{transaction.stockCode}</span>
                        </div>
                      </td>
                      <td>{formatCurrency(transaction.currentPrice)}</td>
                      <td>{formatCurrency(transaction.price)}</td>
                      <td>
                        <div className="profit-info">
                          <span className={`profit-amount ${profit >= 0 ? 'positive' : 'negative'}`}>
                            {formatCurrency(profit)}
                          </span>
                          <span className={`profit-percentage ${profitPercentage >= 0 ? 'positive' : 'negative'}`}>
                            {formatPercentage(profitPercentage)}
                          </span>
                        </div>
                      </td>
                      <td>{transaction.quantity.toLocaleString()}</td>
                      <td>{formatCurrency(currentValue)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Portfolio;