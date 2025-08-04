import React from 'react';
import { useCustomers } from '../../hooks/useCustomers';
import CustomerList from '../../components/customer/CustomerList';


const CustomerListPage = () => {
  const { customers, loading, error, deleteCustomer } = useCustomers();

  

  return (
    <div className="customer-list-page">
      <CustomerList 
        customers={customers}
        loading={loading}
        error={error}
         />
    </div>
  );
};

export default CustomerListPage;