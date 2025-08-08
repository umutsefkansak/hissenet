import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { walletApi } from '../../server/wallet';
import { orderApi } from '../../server/order';
import { getCustomerById } from '../../server/customer';
import { portfolioApi } from '../../server/portfolioApi';
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import { saveAs } from 'file-saver';
import './CustomerDetail.css';

const CustomerDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [customer, setCustomer] = useState(null);
  const [walletBalance, setWalletBalance] = useState(0);
  const [portfolioValue, setPortfolioValue] = useState(0);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [exportDropdownOpen, setExportDropdownOpen] = useState(false);

  useEffect(() => {
    const fetchCustomerData = async () => {
      try {
        setLoading(true);
        const [customerResult, balanceResult, ordersResult, portfoliosResult] = await Promise.all([
          getCustomerById(id),
          walletApi.getCustomerWalletBalance(id),
          orderApi.getOrdersByCustomerId(id),
          portfolioApi.getCustomerPortfolios(id)
        ]);
        
        setCustomer(customerResult.data);
        setWalletBalance(balanceResult.data);
        setOrders(ordersResult.data || []);
        
        const portfolios = portfoliosResult.data || [];
        const totalPortfolioValue = portfolios.reduce((total, portfolio) => {
          return total + (portfolio.totalValue || 0);
        }, 0);
        
        setPortfolioValue(totalPortfolioValue);
        

      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    
    if (id) {
      fetchCustomerData();
    }
  }, [id]);

  const formatDate = (dateString) => {
    if (!dateString) return 'Belirtilmemiş';
    try {
      return new Date(dateString).toLocaleDateString('tr-TR');
    } catch {
      return 'Geçersiz tarih';
    }
  };

  const getCustomerType = (customer) => {
    return customer.customerType === 'INDIVIDUAL' ? 'Bireysel' : 'Kurumsal';
  };

  const getRiskProfile = () => {
    if (!customer || !customer.riskProfile) {
      return 'Belirtilmemiş';
    }
    
    switch (customer.riskProfile.toUpperCase()) {
      case 'CONSERVATIVE':
        return 'Muhafazakar';
      case 'MODERATE':
        return 'Orta Risk';
      case 'AGGRESSIVE':
        return 'Agresif';
      case 'VERY_AGGRESSIVE':
        return 'Çok Agresif';
      default:
        return customer.riskProfile;
    }
  };
  const getRiskProfileClass = (customer) => {
    if (!customer || !customer.riskProfile) {
      return 'unknown';
    }
    
    switch (customer.riskProfile.toUpperCase()) {
      case 'CONSERVATIVE':
        return 'conservative';
      case 'MODERATE':
        return 'moderate';
      case 'AGGRESSIVE':
        return 'aggressive';
      case 'VERY_AGGRESSIVE':
        return 'very-aggressive';
      default:
        return 'unknown';
    }
  };

  const getPortfolioValue = () => {
    return portfolioValue;
  };

  const getCurrentBalance = () => {
    return walletBalance;
  };


  const maskTcNumber = (tcNumber) => {
    if (!tcNumber) return 'Belirtilmemiş';
    
    const tcString = tcNumber.toString();
    if (tcString.length !== 11) return tcNumber;
    

    const maskedPart = '*'.repeat(9);
    const lastTwoDigits = tcString.slice(-2);
    
    return `${maskedPart}${lastTwoDigits}`;
  };

  const getOrderTypeText = (type) => {
    switch (type) {
      case 'BUY':
        return 'Alım';
      case 'SELL':
        return 'Satım';
      default:
        return type;
    }
  };

  const getOrderStatusText = (status) => {
    switch (status) {
      case 'PENDING':
        return 'Beklemede';
      case 'OPEN':
        return 'Beklemede';
      case 'FILLED':
        return 'Onaylandı';
      case 'COMPLETED':
        return 'Tamamlandı';
      case 'CANCELLED':
        return 'İptal Edildi';
      case 'REJECTED':
        return 'Reddedildi';
      default:
        return status;
    }
  };

  const handleBack = () => {
    navigate('/reports');
  };

  if (loading) {
    return (
      <div className="customer-detail-page">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Müşteri bilgileri yükleniyor...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="customer-detail-page">
        <div className="error-container">
          <h2>Hata</h2>
          <p>Müşteri bilgileri yüklenirken hata oluştu: {error}</p>
          <button className="btn-primary" onClick={handleBack}>
            Geri Dön
          </button>
        </div>
      </div>
    );
  }

  if (!customer) {
    return (
      <div className="customer-detail-page">
        <div className="error-container">
          <h2>Müşteri Bulunamadı</h2>
          <p>Belirtilen ID'ye sahip müşteri bulunamadı.</p>
          <button className="btn-primary" onClick={handleBack}>
            Geri Dön
          </button>
        </div>
      </div>
    );
  }
  const handleExportExcel = () => {
    if (!orders.length) return;
    const worksheet = XLSX.utils.json_to_sheet(
      orders.map(order => ({
        Tarih: formatDate(order.createdAt),
        Hisse: order.stockCode,
        'Emir Türü': getOrderTypeText(order.type),
        Durum: getOrderStatusText(order.status),
        Adet: order.quantity,
        Fiyat: order.price,
        'Toplam Tutar': order.totalAmount
      }))
    );
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'İşlemler');
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const file = new Blob([excelBuffer], { type: 'application/octet-stream' });
    saveAs(file, `islem_gecmisi_${customer.firstName}_${customer.lastName}.xlsx`);
    setExportDropdownOpen(false);
  };
  const handleExportPDF = () => {
    if (!orders.length) return;
    
    const pdf = new jsPDF();
    
    // NotoSans font'u ekle
    pdf.addFont('/fonts/NotoSans-Regular.ttf', 'NotoSans', 'normal');
    pdf.setFont('NotoSans');
    
    // Başlık
    pdf.setFontSize(18);
    pdf.setTextColor(30, 55, 72);
    pdf.text('Müşteri İşlem Geçmişi', 20, 30);
    
    // Müşteri bilgileri
    pdf.setFontSize(12);
    pdf.setTextColor(74, 85, 104);
    pdf.text(`Müşteri: ${customer.firstName} ${customer.lastName}`, 20, 45);
    pdf.text(`Tarih: ${new Date().toLocaleDateString('tr-TR')}`, 20, 55);
    
    // Tablo başlıkları
    const headers = ['Tarih', 'Hisse', 'Emir Türü', 'Durum', 'Adet', 'Fiyat', 'Toplam'];
    const startY = 75;
    let currentY = startY;
    
    pdf.setFontSize(10);
    pdf.setTextColor(255, 255, 255);
    pdf.setFillColor(30, 58, 138);
    
    // Header row
    headers.forEach((header, index) => {
      const x = 20 + (index * 25);
      pdf.rect(x, currentY - 8, 25, 8, 'F');
      pdf.text(header, x + 2, currentY - 2);
    });
    
    currentY += 8;
    
    // Data rows
    pdf.setTextColor(45, 55, 72);
    orders.forEach((order, rowIndex) => {
      if (currentY > 250) {
        pdf.addPage();
        currentY = 20;
      }
      
      const rowData = [
        formatDate(order.createdAt),
        order.stockCode,
        getOrderTypeText(order.type),
        getOrderStatusText(order.status),
        order.quantity.toString(),
        `${order.price} ₺`,
        `${order.totalAmount.toLocaleString('tr-TR')} ₺`
      ];
      
      rowData.forEach((cell, index) => {
        const x = 20 + (index * 25);
        pdf.text(cell, x + 2, currentY);
      });
      
      currentY += 6;
    });
    
    pdf.save(`islem_gecmisi_${customer.firstName}_${customer.lastName}.pdf`);
    setExportDropdownOpen(false);
  };


  return (
    <div className="customer-detail-page">
      <div className="customer-detail-content">
        
        <div className="info-section">
        <button className="back-button" onClick={handleBack}>
            ← Geri Dön
          </button>
          <h3>Genel Bilgiler</h3>
          <div className="info-grid">
            <div className="info-item">
              <label>Ad Soyad:</label>
              <span>{customer.firstName} {customer.lastName}</span>
            </div>
            <div className="info-item">
              <label>T.C. Kimlik No:</label>
              <span>{maskTcNumber(customer.tcNumber)}</span>
            </div>
            <div className="info-item">
              <label>Telefon:</label>
              <span>{customer.phone}</span>
            </div>
            <div className="info-item">
              <label>E-posta:</label>
              <span>{customer.email}</span>
            </div>
            <div className="info-item">
              <label>Müşteri Tipi:</label>
              <span className={`customer-type ${customer.customerType.toLowerCase()}`}>
                {getCustomerType(customer)}
              </span>
            </div>
            <div className="info-item">
              <label>Risk Profili:</label>
              <span className={`risk-profile ${getRiskProfileClass(customer)}`}>
                {getRiskProfile(customer)}
              </span>
            </div>
            <div className="info-item">
              <label>Toplam Portföy Değeri:</label>
              <span className="portfolio-value">{getPortfolioValue().toLocaleString('tr-TR')} ₺</span>
            </div>
            <div className="info-item">
              <label>Mevcut Bakiye:</label>
              <span className="current-balance">
                {getCurrentBalance().toLocaleString('tr-TR')} ₺
                <span className="balance-status">✓</span>
              </span>
            </div>
          </div>
        </div>
        <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 16 }}>
       
        </div>

        <div className="transaction-section">
        <div className="transaction-header">
              <h3 className='transaction-title'>İşlem Geçmişi</h3>
              <div className="export-dropdown">
                <button
                  className="export-icon-button"
                  onClick={() => setExportDropdownOpen(!exportDropdownOpen)}
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="7,10 12,15 17,10"/>
                    <line x1="12" y1="15" x2="12" y2="3"/>
                  </svg>
                </button>
                
                {exportDropdownOpen && (
                  <div className="export-dropdown-menu">
                    <button 
                      className="export-dropdown-item"
                      onClick={handleExportExcel}
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                        <polyline points="14,2 14,8 20,8"/>
                        <line x1="16" y1="13" x2="8" y2="13"/>
                        <line x1="16" y1="17" x2="8" y2="17"/>
                        <polyline points="10,9 9,9 8,9"/>
                      </svg>
                      Excel İndir
                    </button>
                    <button 
                      className="export-dropdown-item"
                      onClick={handleExportPDF}
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                        <polyline points="14,2 14,8 20,8"/>
                        <line x1="16" y1="13" x2="8" y2="13"/>
                        <line x1="16" y1="17" x2="8" y2="17"/>
                        <polyline points="10,9 9,9 8,9"/>
                      </svg>
                      PDF İndir
                    </button>
                  </div>
                )}
              </div>
            </div>
          <div className="transaction-table-container">
            <table className="transaction-table">
              <thead>
                <tr>
                  <th>Tarih</th>
                  <th>Hisse</th>
                  <th>Emir Türü</th>
                  <th>Durum</th>
                  <th>Adet</th>
                  <th>Fiyat</th>
                  <th>Toplam Tutar</th>
                </tr>
              </thead>
              <tbody>
                {orders.length > 0 ? (
                  orders.map((order) => (
                    <tr key={order.id}>
                      <td>{formatDate(order.createdAt)}</td>
                      <td>{order.stockCode}</td>
                      <td>
                        <span className={`transaction-type ${order.type.toLowerCase()}`}>
                          {getOrderTypeText(order.type)}
                        </span>
                      </td>
                      <td>
                        <span className={`order-status ${order.status.toLowerCase()}`}>
                          {getOrderStatusText(order.status)}
                        </span>
                      </td>
                      <td>{order.quantity}</td>
                      <td>{order.price} ₺</td>
                      <td>{order.totalAmount.toLocaleString('tr-TR')} ₺</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="7" style={{ textAlign: 'center', padding: '20px' }}>
                      Henüz işlem geçmişi bulunmuyor.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

  
    </div>
  );
};

export default CustomerDetailPage;