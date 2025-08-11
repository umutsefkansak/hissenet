import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getCustomerById } from '../../server/customer';
import { portfolioApi } from '../../server/portfolioApi';
import { orderApi } from '../../server/order';
import UnauthorizedAccess from '../../components/common/UnauthorizedAccess/UnauthorizedAccess';
import './CustomerHome.css';

const CustomerHome = () => {
  const { customerId } = useParams();
  const navigate = useNavigate();
  const [customer, setCustomer] = useState(null);
  const [portfolios, setPortfolios] = useState([]);
  const [stockCount, setStockCount] = useState(0);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editForm, setEditForm] = useState({ phone: '', email: '' });
  const [hasAccess, setHasAccess] = useState(null); 

  const checkCustomerAccess = () => {
    const storedCustomerId = localStorage.getItem('customerId');
    const urlCustomerId = customerId;
    if (!storedCustomerId || storedCustomerId !== urlCustomerId) {
      return false;
    }
    return true;
  };

  useEffect(() => {
    const hasValidAccess = checkCustomerAccess();
    setHasAccess(hasValidAccess);
  }, [customerId]);

  useEffect(() => {
    const fetchData = async () => {
      if (!hasAccess) return;
      try {
        setLoading(true);

        const storedCustomerId = localStorage.getItem('customerId');

        const [customerData, portfoliosData, stockCountData, ordersData] = await Promise.all([
          getCustomerById(customerId),
          portfolioApi.getCustomerPortfolios(customerId),
          portfolioApi.getCustomerStockCount(customerId),
          orderApi.getOrdersByCustomerIdSorted(storedCustomerId)
        ]);

        setCustomer(customerData.data);
        setPortfolios(portfoliosData.data);
        setStockCount(stockCountData.data);
        setOrders(ordersData.data || []);
      } catch (err) {
        setError(err.message);
        console.error('Error fetching customer data:', err);
      } finally {
        setLoading(false);
      }
    };

    if (customerId && hasAccess === true) {
      fetchData();
    }
  }, [customerId, hasAccess]);

  if (hasAccess === null) {
    return (
      <div className="customer-home">
        <div className="loading">Yetki kontrol ediliyor...</div>
      </div>
    );
  }

  if (hasAccess === false) {
    return (
      <UnauthorizedAccess
        title="Yetkisiz Müşteri Erişimi"
        message="Bu müşteri hesabına erişim yetkiniz bulunmamaktadır."
        description="Yalnızca kendi hesabınıza ait sayfalara erişebilirsiniz. Lütfen doğru müşteri hesabı ile giriş yapın."
      />
    );
  }

  const getRiskProfileText = (riskProfile) => {
    const riskMap = {
      CONSERVATIVE: 'Düşük Risk',
      MODERATE: 'Orta Risk',
      HIGH: 'Yüksek Risk',
      AGGRESSIVE: 'Yüksek Risk',
      VERY_AGGRESSIVE: 'Çok Yüksek Risk'
    };
    return riskMap[riskProfile] || riskProfile;
  };

  const getRiskProfileColor = (riskProfile) => {
    const colorMap = {
      CONSERVATIVE: '#10B981',
      MODERATE: '#F59E0B',
      AGGRESSIVE: '#EF4444',
      VERY_AGGRESSIVE: '#DC2626'
    };
    return colorMap[riskProfile] || '#6B7280';
  };

  const formatCurrency = (amount) =>
    new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY', minimumFractionDigits: 2 }).format(amount);

  const formatPercentage = (percentage) => `${percentage > 0 ? '+' : ''}${percentage.toFixed(1)}%`;

  const formatDate = (dateString) => {
    if (!dateString) return 'Belirtilmemiş';
    try {
      return new Date(dateString).toLocaleDateString('tr-TR');
    } catch {
      return 'Geçersiz tarih';
    }
  };

  const getOrderTypeText = (type) => {
    switch (type) {
      case 'BUY': return 'Alım';
      case 'SELL': return 'Satım';
      default: return type;
    }
  };

  const getOrderStatusText = (status) => {
    switch (status) {
      case 'OPEN': return 'Açık';
      case 'FILLED': return 'Gerçekleşti';
      case 'COMPLETED': return 'Tamamlandı';
      case 'CANCELED':
      case 'CANCELLED': return 'İptal Edildi';
      case 'REJECTED': return 'Reddedildi';
      case 'PENDING': return 'Beklemede';
      default: return status;
    }
  };

  const handleEditClick = () => {
    setEditForm({ phone: customer.phone || '', email: customer.email || '' });
    setIsEditModalOpen(true);
  };

  const handleModalClose = () => setIsEditModalOpen(false);

  const handleSave = () => {
    console.log('Saving customer data:', editForm);
    setIsEditModalOpen(false);
    window.showToast('Müşteri bilgileri güncellendi!', 'success', 3000);
  };

  const handleBalanceClick = () => navigate(`/wallet/${customerId}`);

  if (loading) {
    return (
      <div className="customer-home">
        <div className="loading">Yükleniyor...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="customer-home">
        <div className="error">Hata: {error}</div>
      </div>
    );
  }

  if (!customer) {
    return (
      <div className="customer-home">
        <div className="error">Müşteri bulunamadı</div>
      </div>
    );
  }

  const totalPortfolioValue = portfolios.reduce((sum, p) => sum + p.totalValue, 0);
  const totalProfitLoss = portfolios.reduce((sum, p) => sum + p.totalProfitLoss, 0);
  const totalProfitLossPercentage =
    totalPortfolioValue > 0 ? (totalProfitLoss / (totalPortfolioValue - totalProfitLoss)) * 100 : 0;

  const recentOrders = orders.slice(0, 10);

  return (
    <div className="customer-home">
      <div className="customer-header">
        <h1 className="customer-title">Müşteri Sayfası</h1>
        <button className="balance-button" onClick={handleBalanceClick}>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M21 12H3M3 12L10 5M3 12L10 19" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
          Bakiye Yükle
        </button>
      </div>

      <div className="customer-top-section">
        <div className="customer-info-card">
          <div className="customer-info-header">
            <h2 className="customer-name">
              {customer.firstName} {customer.middleName} {customer.lastName}
            </h2>
            <button className="edit-button" onClick={handleEditClick}>Düzenle</button>
          </div>

          <div className="customer-details">
            <div className="detail-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                <circle cx="12" cy="7" r="4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
              <span>T.C. Kimlik No: {customer.tcNumber}</span>
            </div>
            <div className="detail-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 16.92V19.92C22.0011 20.1985 21.9441 20.4742 21.8325 20.7294C21.7209 20.9845 21.5573 21.2136 21.3521 21.4019C21.1469 21.5902 20.9046 21.7335 20.6407 21.8227C20.3769 21.9119 20.0973 21.9454 19.82 21.9209C16.7428 21.5856 13.787 20.5341 11.19 18.85C8.77382 17.3146 6.72533 15.2661 5.18999 12.85C3.49997 10.2412 2.44824 7.27099 2.11999 4.18C2.09544 3.90347 2.12888 3.62461 2.21749 3.36139C2.3061 3.09816 2.44796 2.85638 2.63448 2.65162C2.82099 2.44686 3.04833 2.28362 3.30162 2.17191C3.55491 2.0602 3.82867 2.00223 4.10699 2.00001H7.10699C7.59522 1.99522 8.06562 2.16708 8.43373 2.48353C8.80184 2.79999 9.04201 3.23945 9.10699 3.72001C9.23669 4.68007 9.47144 5.62273 9.80699 6.53001C9.94439 6.88792 9.97348 7.27675 9.89131 7.64959C9.80915 8.02243 9.61981 8.36226 9.34699 8.63001L8.10699 9.88001C9.36131 12.2195 11.2869 14.1451 13.6264 15.3995L14.8764 14.1495C15.1441 13.8767 15.4839 13.6873 15.8568 13.6052C16.2296 13.523 16.6184 13.5521 16.9764 13.6895C17.8837 14.0251 18.8263 14.2598 19.7864 14.3895C20.2698 14.4553 20.712 14.6982 21.0284 15.0703C21.3448 15.4424 21.5146 15.9178 21.5064 16.406L22 16.92Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
              <span>{customer.phone}</span>
            </div>
            <div className="detail-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 4H20C21.1 4 22 4.9 22 6V18C22 19.1 21.1 20 20 20H4C2.9 20 2 19.1 2 18V6C2 4.9 2.9 4 4 4Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                <polyline points="22,6 12,13 2,6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
              <span>{customer.email}</span>
            </div>
          </div>

          <div
            className="risk-badge"
            style={{
              backgroundColor: getRiskProfileColor(customer.riskProfile) === '#10B981' ? '#F0FDF4' :
                              getRiskProfileColor(customer.riskProfile) === '#F59E0B' ? '#FFFBEB' :
                              getRiskProfileColor(customer.riskProfile) === '#EF4444' ? '#FEF2F2' : '#FEF2F2',
              color: getRiskProfileColor(customer.riskProfile),
              borderColor: getRiskProfileColor(customer.riskProfile) === '#10B981' ? '#BBF7D0' :
                          getRiskProfileColor(customer.riskProfile) === '#F59E0B' ? '#FED7AA' :
                          getRiskProfileColor(customer.riskProfile) === '#EF4444' ? '#FECACA' : '#FECACA'
            }}
          >
            {getRiskProfileText(customer.riskProfile)}
          </div>
        </div>

        <div className="portfolio-summary-card">
          <h3 className="portfolio-title">Toplam Portföy Değeri</h3>
          <div className="portfolio-value">{formatCurrency(totalPortfolioValue)}</div>
          <div className={`portfolio-change ${totalProfitLossPercentage >= 0 ? 'positive' : 'negative'}`}>
            {formatPercentage(totalProfitLossPercentage)}
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              {totalProfitLossPercentage >= 0 ? (
                <path d="M7 14L12 9L17 14" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              ) : (
                <path d="M7 10L12 15L17 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              )}
            </svg>
          </div>
          <div className="portfolio-details">
            <span>Hisse Sayısı: {stockCount}</span>
            <span>Son İşlem: {new Date().toLocaleDateString('tr-TR')}</span>
          </div>
        </div>
      </div>

      <div className="recent-transactions-section">
        <h3 className="section-title">Son İşlemler</h3>
        <div className="transaction-table-container">
          <table className="transaction-table">
            <thead>
              <tr>
                <th>Tarih</th>
                <th>Hisse</th>
                <th>Emir türü</th>
                <th>Durum</th>
                <th>Adet</th>
                <th>Fiyat</th>
                <th>Toplam tutar</th>
              </tr>
            </thead>
            <tbody>
              {recentOrders.length > 0 ? (
                recentOrders.map((order) => (
                  <tr key={order.id}>
                    <td>{formatDate(order.createdAt)}</td>
                    <td>{order.stockCode}</td>
                    <td>
                      <span className={`transaction-type ${String(order.type || '').toLowerCase()}`}>
                        {getOrderTypeText(order.type)}
                      </span>
                    </td>
                    <td>
                      <span className={`order-status ${String(order.status || '').toLowerCase()}`}>
                        {getOrderStatusText(order.status)}
                      </span>
                    </td>
                    <td>{order.quantity}</td>
                    <td>{order.price} ₺</td>
                    <td>{Number(order.totalAmount || 0).toLocaleString('tr-TR')} ₺</td>
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

      {isEditModalOpen && (
        <div className="modal-overlay" onClick={handleModalClose}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">Müşteri Bilgilerini Düzenle</h3>
              <button className="modal-close" onClick={handleModalClose}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
              </button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label htmlFor="phone">Telefon Numarası</label>
                <input
                  type="tel"
                  id="phone"
                  value={editForm.phone}
                  onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })}
                  placeholder="Telefon numarası"
                />
              </div>
              <div className="form-group">
                <label htmlFor="email">E-posta</label>
                <input
                  type="email"
                  id="email"
                  value={editForm.email}
                  onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
                  placeholder="E-posta adresi"
                />
              </div>
            </div>
            <div className="modal-footer">
              <button className="modal-button cancel" onClick={handleModalClose}>İptal</button>
              <button className="modal-button save" onClick={handleSave}>Kaydet</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CustomerHome;
