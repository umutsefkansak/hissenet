import React, { useEffect, useState } from 'react';
import DashboardCard from '../common/Card/DashboardCard';
import { orderApi } from '../../server/order';
import { BiBarChartAlt2 } from 'react-icons/bi';

export default function MostActiveStockCard() {
  const [topCode, setTopCode] = useState(null);

  useEffect(() => {
    const fetchTopCode = async () => {
      try {
        const response = await orderApi.getPopularStockCodes();
        const payload = response?.data ?? response;

        if (Array.isArray(payload) && payload.length > 0) {
          const first = payload[0];
          const code =
            typeof first === 'string'
              ? first
              : first?.stockCode || first?.code || first?.symbol || first?.ticker || null;

          setTopCode(code);
        } else {
          setTopCode(null);
        }
      } catch (error) {
        console.error('En popüler hisseler getirilemedi:', error);
        setTopCode(null);
      }
    };

    fetchTopCode();
  }, []);

  return (
    <DashboardCard
      title="En Çok İşlem Gören Hisse"
      value={topCode !== null ? topCode : "Yükleniyor..."}
      subtitle=""
      iconVariant="volume"
      icon={<BiBarChartAlt2 size={24} />}
    />
  );
}
