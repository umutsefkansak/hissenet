import React from 'react';
import DashboardCard from '../common/Card/DashboardCard';
import UsersIcon from '../Icons/UsersIcon';
export default function ActiveCustomerCard() {

  const activeCustomerCount = 1000;

  return (
    <DashboardCard
      title="Aktif Müşteri"
      value={activeCustomerCount.toLocaleString()}
      subtitle=""
      iconVariant="active-users"
      icon={<UsersIcon  width={24} height={24} />}
    />
  );
}
