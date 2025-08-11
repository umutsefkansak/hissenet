import React, { useEffect, useState } from 'react';
import DashboardCard from '../common/Card/DashboardCard';
import { orderApi } from '../../server/order';
import VolumeIcon from '../Icons/VolumeIcon';

export default function TotalTradeVolumeCard() {
  const [dailyVolume, setDailyVolume] = useState(null);

  useEffect(() => {
    const fetchVolume = async () => {
      try {
        const response = await orderApi.getTodayTotalTradeVolume();
        setDailyVolume(response.data);
      } catch (error) {
        console.error('Günlük toplam hacim alınamadı:', error);
      }
    };

    fetchVolume();
  }, []);

  const formattedVolume =
    dailyVolume !== null
      ? `${dailyVolume.toLocaleString('tr-TR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}₺`
      : "Yükleniyor...";

  return (
    <DashboardCard
      title="Günlük Toplam Hacim"
      value={formattedVolume}
      subtitle=""
      iconVariant="volume"
      icon={<VolumeIcon width={24} height={24} />}
    />
  );
}
