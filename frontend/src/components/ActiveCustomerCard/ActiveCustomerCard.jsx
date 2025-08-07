import React from 'react';
import DashboardCard from '../common/Card/DashboardCard';

export default function ActiveCustomerCard() {

  const activeCustomerCount = 1000;

  return (
    <DashboardCard
      title="Aktif Müşteri"
      value={activeCustomerCount.toLocaleString()}
      subtitle=""
      iconVariant="users"
      icon={<div style={{ fontSize: 20 }}>👥</div>}
    />
  );
}
