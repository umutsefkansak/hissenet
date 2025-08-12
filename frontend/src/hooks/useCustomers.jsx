import { useState, useEffect } from 'react';
import { getAllCustomers, updateIndividualCustomer, updateCorporateCustomer } from '../server/customer';

export const useCustomers = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchCustomers = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const result = await getAllCustomers();
      setCustomers(result.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  

  const updateCustomer = async (updateData) => {
    try {
      const { id, customerType, ...data } = updateData;
  
      if (customerType === 'CORPORATE') {
        await updateCorporateCustomer(id, data);
      } else {
        await updateIndividualCustomer(id, data);
      }
  
      await fetchCustomers();
      return true;
    } catch (err) {
      console.error('Update customer error:', err);
      setError(err.message);
      return false;
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  return {
    customers,
    loading,
    error,
    refreshCustomers: fetchCustomers,
    updateCustomer
  };
};