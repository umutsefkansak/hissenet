import React from 'react';
import { useNavigate } from 'react-router-dom';
import './CustomerList.css';

const CustomerList = ({ customers = [], loading, error, onDelete }) => {
  const navigate = useNavigate();

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

  const getKycStatus = (customer) => {
    return customer.kycVerified ? 'Doğrulanmış' : 'Doğrulanmamış';
  };

  const handleViewCustomer = (customerId) => {
    navigate(`/customers/${customerId}`);
  };

  if (loading) {
    return (
      <div className="customer-list-loading">
        <div className="loading-spinner"></div>
        <p>Müşteriler yükleniyor...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="customer-list-error">
        <p>Hata: {error}</p>
        <button onClick={() => window.location.reload()}>Tekrar Dene</button>
      </div>
    );
  }

  const customerList = customers || [];

  return (
    <div className="customer-list">
      <div className="customer-list-header">
        <h2>Müşteri Listesi</h2>
        <span className="customer-count">Toplam: {customerList.length} müşteri</span>
      </div>

      <div className="customer-table-container">
        <table className="customer-table">
          <thead>
            <tr>
              <th>Ad Soyad</th>
              <th>Email</th>
              <th>Telefon</th>
              <th>Müşteri Tipi</th>
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {customerList.map((customer) => (
              <tr key={customer.id}>
                <td>
                  {customer.customerType === 'INDIVIDUAL' 
                    ? `${customer.firstName} ${customer.lastName}`
                    : customer.companyName
                  }
                </td>
                <td>{customer.email}</td>
                <td>{customer.phone}</td>
                <td>
                  <span className={`customer-type ${customer.customerType.toLowerCase()}`}>
                    {getCustomerType(customer)}
                  </span>
                </td>
                <td>
                  <div className="customer-actions">
                    <button 
                      className="btn-view"
                      onClick={() => handleViewCustomer(customer.id)}
                    >
                      Görüntüle
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default CustomerList;