import React, { useEffect, useState } from 'react';
import DashboardCard from '../common/Card/DashboardCard';
import { orderApi } from '../../server/order';
import VolumeIcon from '../Icons/VolumeIcon';

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
      iconVariant="volume"
      icon={<VolumeIcon width={24} height={24} />}
    />
  );
}
