import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { portfolioApi } from '../../server/portfolioApi';
import { walletApi } from '../../server/wallet';
import './Portfolio.css';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';

const Portfolio = () => {
  const { customerId = '68' } = useParams(); // Default to 68 for testing
  const [portfolios, setPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [stockTransactions, setStockTransactions] = useState([]);
  const [walletBalance, setWalletBalance] = useState(0);
  const [blockedBalance, setBlockedBalance] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('portfolio'); // 'portfolio' veya 'history'
  const [historyPage, setHistoryPage] = useState(0);
  const [portfolioPage, setPortfolioPage] = useState(0);

  // Fetch portfolios and wallet balance on component mount
  useEffect(() => {
    fetchPortfolios();
    fetchWalletBalance();
    fetchBlockedBalance();
  }, [customerId]);

  // Fetch stock transactions when selected portfolio changes
  useEffect(() => {
    if (selectedPortfolio) {
      fetchStockTransactions(selectedPortfolio.id);
    }
  }, [selectedPortfolio]);

  useEffect(() => {
    setPortfolioPage(0); // Portföy değişince ilk sayfaya dön
    setHistoryPage(0);
  }, [selectedPortfolio, activeTab]);

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

  const fetchBlockedBalance = async () => {
    try {
      const response = await walletApi.getBlockedBalance(customerId);
      if (response !== undefined) {
        setBlockedBalance(response.data);
      }
    } catch (err) {
      console.error('Error fetching blocked balance:', err);
      setBlockedBalance(0);
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
      blockedBalance: blockedBalance // Artık state'ten geliyor
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
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 12, marginBottom: 16 }}>
            <button
              className="tab-arrow"
              onClick={() => setActiveTab('portfolio')}
              disabled={activeTab === 'portfolio'}
              aria-label="Portföy İçeriği"
            >
              <FaChevronLeft size={22} />
            </button>
            <h3 style={{ minWidth: 180, textAlign: 'center', margin: 0 }}>
              {activeTab === 'portfolio' ? 'Portföy İçeriği' : 'Hisse Geçmişi'}
            </h3>
            <button
              className="tab-arrow"
              onClick={() => setActiveTab('history')}
              disabled={activeTab === 'history'}
              aria-label="Hisse Geçmişi"
            >
              <FaChevronRight size={22} />
            </button>
          </div>
          <div className="portfolio-table">
            {activeTab === 'portfolio' ? (
              <>
              <table>
                <thead>
                  <tr>
                    <th>Hisse</th>
                    <th>Güncel Fiyat</th>
                    <th>Ortalama Alış Fiyatı</th>
                    <th>Kar/Zarar</th>
                    <th>Adet</th>
                    <th>Güncel Değer</th>
                  </tr>
                </thead>
                <tbody>
                  {stockTransactions
                    .slice(portfolioPage * 5, portfolioPage * 5 + 5)
                    .map((transaction, index) => {
                      const currentValue = transaction.quantity * transaction.currentPrice;
                      const profit = currentValue - transaction.totalAmount;
                      const profitPercentage = transaction.totalAmount > 0 ? (profit / transaction.totalAmount) * 100 : 0;
                      return (
                        <tr key={index}>
                          <td>
                            <div className="stock-info">
                              <span className="stock-symbol">{transaction.stockCode}</span>
                            </div>f
                          </td>
                          <td>{formatCurrency(transaction.currentPrice)}</td>
                          <td>{formatCurrency(transaction.price)}</td>
                          <td>
                            <div className="profit-info">
                              <span className={`profit-amount ${profit >= 0 ? 'positive' : 'negative'}`}>{formatCurrency(profit)}</span>
                              <span className={`profit-percentage ${profitPercentage >= 0 ? 'positive' : 'negative'}`}>{formatPercentage(profitPercentage)}</span>
                            </div>
                          </td>
                          <td>{transaction.quantity.toLocaleString()}</td>
                          <td>{formatCurrency(currentValue)}</td>
                        </tr>
                      );
                    })}
                </tbody>
              </table>
              {/* Pagination Controls */}
              {stockTransactions.length > 5 && (
                <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 16, marginTop: 16 }}>
                  <button
                    className="tab-arrow"
                    onClick={() => setPortfolioPage(p => Math.max(0, p - 1))}
                    disabled={portfolioPage === 0}
                    aria-label="Önceki Sayfa"
                  >
                    <FaChevronLeft size={20} />
                  </button>
                  <span style={{ fontWeight: 600, color: '#1e3a8a' }}>
                    {portfolioPage + 1} / {Math.ceil(stockTransactions.length / 5)}
                  </span>
                  <button
                    className="tab-arrow"
                    onClick={() => setPortfolioPage(p => Math.min(Math.ceil(stockTransactions.length / 5) - 1, p + 1))}
                    disabled={portfolioPage >= Math.ceil(stockTransactions.length / 5) - 1}
                    aria-label="Sonraki Sayfa"
                  >
                    <FaChevronRight size={20} />
                  </button>
                </div>
              )}
              </>
            ) : (
              <>
              <table>
                <thead>
                  <tr>
                    <th>Tarih</th>
                    <th>Hisse</th>
                    <th>İşlem Tipi</th>
                    <th>Adet</th>
                    <th>Fiyat</th>
                    <th>Toplam Tutar</th>
                    <th>Komisyon</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedPortfolio && selectedPortfolio.list && selectedPortfolio.list.length > 0 ? (
                    selectedPortfolio.list
                      .slice()
                      .sort((a, b) => new Date(b.transactionDate) - new Date(a.transactionDate))
                      .slice(historyPage * 5, historyPage * 5 + 5)
                      .map((item, idx) => (
                        <tr key={item.id || idx}>
                          <td>{item.transactionDate ? new Date(item.transactionDate).toLocaleString('tr-TR') : '-'}</td>
                          <td>{item.stockCode}</td>
                          <td>{item.transactionType === 'BUY' ? 'Alış' : item.transactionType === 'SELL' ? 'Satış' : '-'}</td>
                          <td>{item.quantity}</td>
                          <td>{formatCurrency(item.price)}</td>
                          <td>{formatCurrency(item.totalAmount)}</td>
                          <td>{formatCurrency(item.commission)}</td>
                        </tr>
                      ))
                  ) : (
                    <tr><td colSpan={7} style={{textAlign:'center'}}>İşlem bulunamadı</td></tr>
                  )}
                </tbody>
              </table>
              {/* Pagination Controls */}
              {selectedPortfolio && selectedPortfolio.list && selectedPortfolio.list.length > 5 && (
                <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 16, marginTop: 16 }}>
                  <button
                    className="tab-arrow"
                    onClick={() => setHistoryPage(p => Math.max(0, p - 1))}
                    disabled={historyPage === 0}
                    aria-label="Önceki Sayfa"
                  >
                    <FaChevronLeft size={20} />
                  </button>
                  <span style={{ fontWeight: 600, color: '#1e3a8a' }}>
                    {historyPage + 1} / {Math.ceil(selectedPortfolio.list.length / 5)}
                  </span>
                  <button
                    className="tab-arrow"
                    onClick={() => setHistoryPage(p => Math.min(Math.ceil(selectedPortfolio.list.length / 5) - 1, p + 1))}
                    disabled={historyPage >= Math.ceil(selectedPortfolio.list.length / 5) - 1}
                    aria-label="Sonraki Sayfa"
                  >
                    <FaChevronRight size={20} />
                  </button>
                </div>
              )}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Portfolio;