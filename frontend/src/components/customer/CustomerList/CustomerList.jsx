import React, { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import CustomerUpdateModal from '../CustomerEditModal/CustomerUpdateModal';
import TodayTotalTradeVolumeCard from '../../TodayTotalTradeVolume/TodayTotalTradeVolume';
import ActiveCustomerCard from '../../ActiveCustomerCard/ActiveCustomerCard';
import MostActiveStockCard from '../../MostActiveStock/MostActiveStock';
import EditButton from '../../common/Button/EditButton';
import Pagination from '../../common/Pagination/Pagination';
import SortableHeader from '../../common/Sorting/SortableHeader';
import { sortList } from '../../common/Sorting/sortUtils';
import './CustomerList.css';

const CustomerList = ({ customers = [], loading, error, onUpdate }) => {
  const navigate = useNavigate();
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(5);

  const [sortConfig, setSortConfig] = useState({ key: 'id', direction: 'asc' });

  const shouldShowCards = !loading && !error;

  const accessors = {
    name: (c) =>
      c.customerType === 'INDIVIDUAL'
        ? `${c.firstName || ''} ${c.lastName || ''}`.trim().toLowerCase()
        : (c.companyName || '').toLowerCase(),
    email: (c) => (c.email || '').toLowerCase(),
    phone: (c) => (c.phone || '').toLowerCase(),
    customerType: (c) => (c.customerType || '').toLowerCase(),
    id: (c) => Number(c.id) || 0,
  };

  const handleSort = (key) => {
    setSortConfig((prev) => ({
      key,
      direction: prev.key === key && prev.direction === 'asc' ? 'desc' : 'asc',
    }));
    setPage(0);
  };

  const filteredAndSortedCustomers = useMemo(() => {
    let filtered = customers;
    if (searchTerm.trim()) {
      filtered = customers.filter((customer) => {
        const fullName =
          customer.customerType === 'INDIVIDUAL'
            ? `${customer.firstName} ${customer.lastName}`.toLowerCase()
            : (customer.companyName || '').toLowerCase();
        return fullName.startsWith(searchTerm.toLowerCase());
      });
    }
    return sortList(filtered, sortConfig.key, sortConfig.direction, accessors);
  }, [customers, searchTerm, sortConfig]);

  const paginatedCustomers = useMemo(() => {
    const start = page * pageSize;
    return filteredAndSortedCustomers.slice(start, start + pageSize);
  }, [filteredAndSortedCustomers, page, pageSize]);

  const totalPages = Math.ceil(filteredAndSortedCustomers.length / pageSize);

  const handleViewCustomer = (customerId) => navigate(`/customers/${customerId}`);
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
            <button className="clear-search" onClick={() => setSearchTerm('')}>
              ×
            </button>
          )}
        </div>
      </div>

      <div className="customer-table-container">
        <table className="customer-table">
          <thead>
            <tr>
              <SortableHeader columnKey="name" label="Ad Soyad" sortConfig={sortConfig} onSort={handleSort} />
              <SortableHeader columnKey="email" label="Email" sortConfig={sortConfig} onSort={handleSort} />
              <SortableHeader columnKey="phone" label="Telefon" sortConfig={sortConfig} onSort={handleSort} />
              <SortableHeader columnKey="customerType" label="Müşteri Tipi" sortConfig={sortConfig} onSort={handleSort} />
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {paginatedCustomers.map((customer) => (
              <tr key={customer.id}>
                <td>
                  {customer.customerType === 'INDIVIDUAL'
                    ? `${customer.firstName} ${customer.lastName}`
                    : customer.companyName}
                </td>
                <td>{customer.email}</td>
                <td>{customer.phone}</td>
                <td>
                  <span className={`customer-type ${customer.customerType.toLowerCase()}`}>
                    {customer.customerType === 'INDIVIDUAL' ? 'Bireysel' : 'Kurumsal'}
                  </span>
                </td>
                <td>
                  <div className="customer-actions">
                    <button className="btn-view" onClick={() => handleViewCustomer(customer.id)}>
                      Görüntüle
                    </button>
                    <EditButton onClick={() => handleUpdateCustomer(customer)} />
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination
        currentPage={page}
        totalPages={totalPages}
        totalElements={filteredAndSortedCustomers.length}
        pageSize={pageSize}
        onPageChange={(p) => setPage(p)}
        onPageSizeChange={(newSize) => {
          setPageSize(newSize);
          setPage(0);
        }}
      />

      <CustomerUpdateModal
        isOpen={isUpdateModalOpen}
        onClose={handleCloseUpdateModal}
        customer={selectedCustomer}
        onUpdate={handleUpdateSubmit}
        customers={customers}
      />
    </div>
  );
};

export default CustomerList;