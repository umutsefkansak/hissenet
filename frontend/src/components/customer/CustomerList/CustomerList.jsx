import React, { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import CustomerUpdateModal from '../CustomerEditModal/CustomerUpdateModal';
import TodayTotalTradeVolumeCard from '../../TodayTotalTradeVolume/TodayTotalTradeVolume';
import ActiveCustomerCard from '../../ActiveCustomerCard/ActiveCustomerCard';
import MostActiveStockCard from '../../MostActiveStock/MostActiveStock';
import EditButton from '../../common/Button/EditButton';
import './CustomerList.css';

const CustomerList = ({ customers = [], loading, error, onUpdate }) => {
  const navigate = useNavigate();
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
 
  const [currentPage, setCurrentPage] = useState(1);
  const [customersPerPage] = useState(8);
  
  const [sortConfig, setSortConfig] = useState({
    key: 'id',
    direction: 'asc'
  });

  const shouldShowCards = !loading && !error;

  const getCustomerType = (customer) => {
    return customer.customerType === 'INDIVIDUAL' ? 'Bireysel' : 'Kurumsal';
  };

  const sortCustomers = (customers, key, direction) => {
    return [...customers].sort((a, b) => {
      let aValue, bValue;
  
      if (key === 'name') {
        aValue = a.customerType === 'INDIVIDUAL'
          ? `${a.firstName} ${a.lastName}`.toLowerCase()
          : a.companyName.toLowerCase();
        bValue = b.customerType === 'INDIVIDUAL'
          ? `${b.firstName} ${b.lastName}`.toLowerCase()
          : b.companyName.toLowerCase();
      } else if (typeof a[key] === 'number' || typeof b[key] === 'number') {
        aValue = Number(a[key]) || 0;
        bValue = Number(b[key]) || 0;
      } else {
        aValue = (a[key] ?? '').toString().toLowerCase();
        bValue = (b[key] ?? '').toString().toLowerCase();
      }
  
      if (aValue === bValue) return 0;
      return direction === 'asc' ? (aValue > bValue ? 1 : -1) : (aValue < bValue ? 1 : -1);
    });
  };

  const handleSort = (key) => {
    setSortConfig(prevConfig => ({
      key,
      direction: prevConfig.key === key && prevConfig.direction === 'asc' ? 'desc' : 'asc'
    }));
    setCurrentPage(1); 
  };

  const filteredAndSortedCustomers = useMemo(() => {
    let filtered = customers;
    
    if (searchTerm.trim()) {
      filtered = customers.filter(customer => {
        const fullName = customer.customerType === 'INDIVIDUAL' 
          ? `${customer.firstName} ${customer.lastName}`.toLowerCase()
          : customer.companyName.toLowerCase();
        
        return fullName.startsWith(searchTerm.toLowerCase());
      });
    }
    
    return sortCustomers(filtered, sortConfig.key, sortConfig.direction);
  }, [customers, searchTerm, sortConfig]);

  const getPaginatedCustomers = () => {
    const indexOfLastCustomer = currentPage * customersPerPage;
    const indexOfFirstCustomer = indexOfLastCustomer - customersPerPage;
    return filteredAndSortedCustomers.slice(indexOfFirstCustomer, indexOfLastCustomer);
  };

  const totalPages = Math.ceil(filteredAndSortedCustomers.length / customersPerPage);
  
  const goToPage = (page) => {
    setCurrentPage(page);
  };

  const goToPreviousPage = () => {
    setCurrentPage(prev => Math.max(prev - 1, 1));
  };

  const goToNextPage = () => {
    setCurrentPage(prev => Math.min(prev + 1, totalPages));
  };

  const SortIndicator = ({ columnKey }) => {
    if (sortConfig.key !== columnKey) {
      return <span className="sort-indicator">↕</span>;
    }
    return (
      <span className={`sort-indicator ${sortConfig.direction === 'asc' ? 'asc' : 'desc'}`}>
        {sortConfig.direction === 'asc' ? '↑' : '↓'}
      </span>
    );
  };

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
    await onUpdate(updateData); 
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
      
      {shouldShowCards && (
        <div className="summary-cards">
          <div className="summary-card">
            <TodayTotalTradeVolumeCard />
          </div>
          <div className="summary-card">
            <ActiveCustomerCard />
          </div>
          <div className="summary-card">
            <MostActiveStockCard />
          </div>
        </div>
      )}

      <div className="customer-list-header">
        <h2>Müşteri Listesi</h2>
      </div>

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
              <th 
                className="sortable-header"
                onClick={() => handleSort('name')}
              >
                Ad Soyad <SortIndicator columnKey="name" />
              </th>
              <th 
                className="sortable-header"
                onClick={() => handleSort('email')}
              >
                Email <SortIndicator columnKey="email" />
              </th>
              <th 
                className="sortable-header"
                onClick={() => handleSort('phone')}
              >
                Telefon <SortIndicator columnKey="phone" />
              </th>
              <th 
                className="sortable-header"
                onClick={() => handleSort('customerType')}
              >
                Müşteri Tipi <SortIndicator columnKey="customerType" />
              </th>
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {getPaginatedCustomers().map((customer) => (
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
                    <EditButton 
                      onClick={() => handleUpdateCustomer(customer)}
                    />
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {filteredAndSortedCustomers.length > customersPerPage && (
        <div className="pagination-container">
          <div className="pagination-info">
            {((currentPage - 1) * customersPerPage) + 1} - {Math.min(currentPage * customersPerPage, filteredAndSortedCustomers.length)} / {filteredAndSortedCustomers.length} müşteri
          </div>
          <div className="pagination-controls">
            <button 
              className="pagination-button"
              onClick={goToPreviousPage}
              disabled={currentPage === 1}
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="15,18 9,12 15,6"/>
              </svg>
            </button>
            
            <div className="page-numbers">
              {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
                <button
                  key={page}
                  className={`page-number ${currentPage === page ? 'active' : ''}`}
                  onClick={() => goToPage(page)}
                >
                  {page}
                </button>
              ))}
            </div>
            
            <button 
              className="pagination-button"
              onClick={goToNextPage}
              disabled={currentPage === totalPages}
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="9,18 15,12 9,6"/>
              </svg>
            </button>
          </div>
        </div>
      )}

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