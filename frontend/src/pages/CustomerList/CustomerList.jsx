import React from 'react';
import { useCustomers } from '../../hooks/useCustomers';
import CustomerList from '../../components/customer/CustomerList';


const CustomerListPage = () => {
  const { customers, loading, error, deleteCustomer } = useCustomers();

  const handleDelete = async (customerId) => {
    const success = await deleteCustomer(customerId);
    if (success) {
  
      window.showToast('Müşteri başarıyla silindi!', 'success', 3000);
    } else {
      window.showToast('Müşteri silinirken hata oluştu!', 'error', 3000);
    }
  };

  return (
    <div className="customer-list-page">
      <CustomerList 
        customers={customers}
        loading={loading}
        error={error}
        onDelete={handleDelete}
      />
    </div>
  );
};

export default CustomerListPage;