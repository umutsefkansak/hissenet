import React, { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import CustomerUpdateModal from '../CustomerEditModal/CustomerUpdateModal';
import './CustomerList.css';

const CustomerList = ({ customers = [], loading, error, onDelete, onUpdate }) => {
  const navigate = useNavigate();
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  const getCustomerType = (customer) => {
    return customer.customerType === 'INDIVIDUAL' ? 'Bireysel' : 'Kurumsal';
  };

  // Arama filtreleme - ilk harfle başlayan müşteriler
  const filteredCustomers = useMemo(() => {
    if (!searchTerm.trim()) {
      return customers;
    }
    
    return customers.filter(customer => {
      const fullName = customer.customerType === 'INDIVIDUAL' 
        ? `${customer.firstName} ${customer.lastName}`.toLowerCase()
        : customer.companyName.toLowerCase();
      
      // İlk harfle başlayan müşterileri filtrele
      return fullName.startsWith(searchTerm.toLowerCase());
    });
  }, [customers, searchTerm]);

  const handleViewCustomer = (customerId) => {
    navigate(`/customers/${customerId}`);
  };

  const handleUpdateCustomer = (customer) => {
    setSelectedCustomer(customer);
    setIsUpdateModalOpen(true);
  };

  const handleCloseUpdateModal = () => {
    setIsUpdateModalOpen(false);
    setSelectedCustomer(null);
  };

  const handleUpdateSubmit = async (updateData) => {
    try {
      await onUpdate(updateData);
      window.showToast && window.showToast('Müşteri başarıyla güncellendi!', 'success', 3000);
    } catch (error) {
      window.showToast && window.showToast('Müşteri güncellenirken hata oluştu!', 'error', 3000);
      throw error;
    }
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

  return (
    <div className="customer-list">
      <div className="customer-list-header">
        <h2>Müşteri Listesi</h2>
        <span className="customer-count">Toplam: {filteredCustomers.length} müşteri</span>
      </div>

      {/* Arama Çubuğu */}
      <div className="search-container">
        <div className="search-box">
          <svg className="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="11" cy="11" r="8"></circle>
            <path d="m21 21-4.35-4.35"></path>
          </svg>
          <input
            type="text"
            placeholder="Müşteri adının ilk harfini yazın..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input-customer"
          />
          {searchTerm && (
            <button 
              className="clear-search"
              onClick={() => setSearchTerm('')}
            >
              ×
            </button>
          )}
        </div>
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
            {filteredCustomers.map((customer) => (
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
                    <button 
                      className="btn-update"
                      onClick={() => handleUpdateCustomer(customer)}
                    >
                      Güncelle
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <CustomerUpdateModal
        isOpen={isUpdateModalOpen}
        onClose={handleCloseUpdateModal}
        customer={selectedCustomer}
        onUpdate={handleUpdateSubmit}
      />
    </div>
  );
};

export default CustomerList;