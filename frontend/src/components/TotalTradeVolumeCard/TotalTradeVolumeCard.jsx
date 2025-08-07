import React, { useEffect, useState } from 'react';
import DashboardCard from '../common/Card/DashboardCard';
import { orderApi } from '../../server/order';

export default function TotalTradeVolumeCard() {
  const [dailyVolume, setDailyVolume] = useState(null);

  useEffect(() => {
    const fetchVolume = async () => {
      try {
        const response = await orderApi.getTotalTradeVolume();
        setDailyVolume(response.data);
      } catch (error) {
        console.error('Toplam hacim alınamadı:', error);
      }
    };

    fetchVolume();
  }, []);

  return (
    <DashboardCard
      title="Toplam Hacim"
      value={dailyVolume !== null ? `${dailyVolume}₺` : "Yükleniyor..."}
      subtitle=""
      iconVariant="users"
      icon={<div style={{ fontSize: 20 }}>💰</div>}
    />
  );
}
