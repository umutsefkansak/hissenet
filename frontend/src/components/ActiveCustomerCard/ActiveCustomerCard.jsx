import React, { useEffect, useState } from 'react';
import DashboardCard from '../common/Card/DashboardCard';
import UsersIcon from '../Icons/UsersIcon';
import * as customerApi from '../../server/customer';

export default function ActiveCustomerCard() {
  const [activeCustomerCount, setActiveCustomerCount] = useState(null);

  useEffect(() => {
    let alive = true;

    const fetchCustomers = async () => {
      try {
        const res = await customerApi.getAllCustomers();
        
        const list =
          Array.isArray(res) ? res
          : Array.isArray(res?.data) ? res.data
          : Array.isArray(res?.result) ? res.result
          : [];

        const total =
          Number.isFinite(res?.total) ? res.total : list.length;

        if (!alive) return;
        setActiveCustomerCount(total);
      } catch (err) {
        console.error('Müşteri listesi alınamadı:', err);
        if (!alive) return;
        setActiveCustomerCount(null);
      }
    };

    fetchCustomers();
    return () => { alive = false; };
  }, []);

  return (
    <DashboardCard
      title="Aktif Müşteri"
      className='no-hover'
      value={
        activeCustomerCount !== null
          ? activeCustomerCount.toLocaleString('tr-TR')
          : 'Yükleniyor...'
      }
      subtitle=""
      iconVariant="active-users"
      icon={<UsersIcon width={24} height={24} />}
    />
  );
}
