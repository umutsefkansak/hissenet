import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { walletApi } from '../../server/wallet';
import { orderApi } from '../../server/order';
import { getCustomerById } from '../../server/customer';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import './CustomerDetail.css';

const CustomerDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [customer, setCustomer] = useState(null);
  const [walletBalance, setWalletBalance] = useState(0);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [exportMenuOpen, setExportMenuOpen] = useState(false);

  useEffect(() => {
    const fetchCustomerData = async () => {
      try {
        setLoading(true);
        
        const customerResult = await getCustomerById(id);
        setCustomer(customerResult.data);
        
        const balanceResult = await walletApi.getCustomerWalletBalance(id);
        setWalletBalance(balanceResult.data);
        
        const ordersResult = await orderApi.getOrdersByCustomerId(id);
        setOrders(ordersResult.data || []);
        
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
    return 'Düşük';
  };

  const getPortfolioValue = () => {
    return 180000;
  };

  const getCurrentBalance = () => {
    return walletBalance;
  };

  // TC Kimlik No maskeleme fonksiyonu
  const maskTcNumber = (tcNumber) => {
    if (!tcNumber) return 'Belirtilmemiş';
    
    const tcString = tcNumber.toString();
    if (tcString.length !== 11) return tcNumber;
    
    // İlk 9 haneyi * ile maskele, son 2 haneyi göster
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
    navigate('/customers');
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
    setExportMenuOpen(false);
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
              <span className="risk-profile low">{getRiskProfile()}</span>
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
          <button
            className="export-button"
            onClick={handleExportExcel}
          >
            İşlem Geçmişini İndir
          </button>
        </div>

        <div className="transaction-section">
          <h3>İşlem Geçmişi</h3>
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