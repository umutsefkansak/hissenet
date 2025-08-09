import React from 'react';
import { useCustomers } from '../../hooks/useCustomers';
import CustomerList from '../../components/customer/CustomerList';

const CustomerListPage = () => {
  const { customers, loading, error, updateCustomer } = useCustomers();

  

  const handleUpdate = async (updateData) => {
    const success = await updateCustomer(updateData);
    if (success) {
      window.showToast('Müşteri başarıyla güncellendi!', 'success', 3000);
    } else {
      window.showToast('Müşteri güncellenirken hata oluştu!', 'error', 3000);
    }
    return success;
  };

  return (
    <div className="customer-list-page">
      <CustomerList 
        customers={customers}
        loading={loading}
        error={error}
        onUpdate={handleUpdate}
      />
    </div>
  );
};

export default CustomerListPage;