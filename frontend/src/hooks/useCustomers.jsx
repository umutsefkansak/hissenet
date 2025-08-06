import { useState, useEffect } from 'react';
import { getAllCustomers, updateIndividualCustomer } from '../server/customer';

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
      const { id, ...data } = updateData;
      
      console.log('Updating customer with data:', { id, data });
      
      // Sadece individual customer güncellemesi yapıyoruz
      await updateIndividualCustomer(id, data);
      
      // Listeyi yenile
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