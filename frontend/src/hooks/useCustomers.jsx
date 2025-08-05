import { useState, useEffect } from 'react';
import { customerApi } from '../services/api/customerApi';

export const useCustomers = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchCustomers = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const result = await customerApi.getAllCustomers();
      setCustomers(result.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const deleteCustomer = async (id) => {
    try {
      await customerApi.deleteCustomer(id);
      // Listeyi yenile
      await fetchCustomers();
      return true;
    } catch (err) {
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
    deleteCustomer
  };
};