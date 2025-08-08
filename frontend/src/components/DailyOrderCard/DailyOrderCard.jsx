import React, { useEffect, useState } from 'react';
import DashboardCard from '../common/Card/DashboardCard';
import { orderApi } from '../../server/order';
import { BiBarChartAlt2 } from 'react-icons/bi';

export default function DailyOrderCard() {
  const [todayOrderCount, setTodayOrderCount] = useState(null);

  useEffect(() => {
    const fetchTodayOrderCount = async () => {
      try {
        const response = await orderApi.getTodayOrderCount();
        setTodayOrderCount(response.data);
      } catch (error) {
        console.error('Günlük işlem sayısı alınamadı:', error);
      }
    };

    fetchTodayOrderCount();
  }, []);

  return (
    <DashboardCard
      title="Günlük İşlem"
      value={todayOrderCount !== null ? todayOrderCount : "Yükleniyor..."}
      subtitle=""
      iconVariant="daily"
      icon={<BiBarChartAlt2 size={24} />}
    />
  );
}
