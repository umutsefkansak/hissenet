import React, { useEffect, useState } from "react";
import "./Dashboard.css";
import PopularStocks from "../../components/PopularStocks/PopularStocks";
import Bist100Card from "../../components/Bist100/Bist100";
import { orderApi } from "../../server/order";

const Dashboard = () => {
  const [dailyVolume, setDailyVolume] = useState(null);
  const [recentOrders, setRecentOrders] = useState([]);
  const [allFilledOrders, setAllFilledOrders] = useState([]);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const volumeResponse = await orderApi.getTotalTradeVolume();
        const recentResponse = await orderApi.getLastFiveOrders();
        setDailyVolume(volumeResponse.data);
        setRecentOrders(recentResponse.data);
      } catch (error) {
        console.error("Dashboard verileri alınamadı:", error);
      }
    };

    fetchData();
  }, []);

  const handleShowModal = async () => {
    try {
      const response = await orderApi.getTodayFilledOrders();
      setAllFilledOrders(response.data);
      setShowModal(true);
    } catch (error) {
      console.error("Tüm işlemler getirilemedi:", error);
    }
  };

  return (
    <div className="dashboard">
      <h1 className="title">Hoş Geldiniz</h1>

      <div className="summaryCards">

         <Bist100Card />

        <div className="card">
          <span className="cardTitle">BIST 100</span>
          <span className="cardValue">Günlük İşlem<br />147</span>
        </div>
        <div className="card">
          <span className="cardTitle">Toplam Hacim</span>
          <span className="cardValue">
            {dailyVolume !== null ? `${dailyVolume} ₺` : 'Yükleniyor...'}
          </span>
        </div>
        <div className="card">
          <span className="cardTitle">Aktif Müşteri</span>
          <span className="cardValue">1.000</span>
        </div>
      </div>

      <div className="bottomSection">

    <PopularStocks  className="noMaxWidth"/>


        <div className="recentTransactions">
          <div className="transactionsHeader">
            <h3>Son İşlemler</h3>
            <button className="viewAll" onClick={handleShowModal}>Tümünü Gör</button>
          </div>
          <table className="transactionsTable">
            <tbody>
              {recentOrders.length > 0 ? (
                recentOrders.map((order, index) => (
                  <tr key={index}>
                    <td><strong>{order.stockCode}</strong> {order.type === 'BUY' ? 'alış' : 'satış'}</td>
                    <td className="right">{Number(order.totalAmount).toLocaleString()} TL</td>
                  </tr>
                ))
              ) : (
                <tr><td colSpan={2}>Yükleniyor...</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

     {showModal && (
  <div className="dashboardModalOverlay" onClick={() => setShowModal(false)}>
    <div className="dashboardModalContent" onClick={(e) => e.stopPropagation()}>
      <h2>Tüm İşlemler</h2>
      <table className="transactionsTable full">
        <tbody>
          {allFilledOrders.length > 0 ? (
            allFilledOrders.map((order, index) => (
              <tr key={index}>
                <td><strong>{order.stockCode}</strong> {order.type === 'BUY' ? 'alış' : 'satış'}</td>
                <td className="right">{Number(order.totalAmount).toLocaleString()} TL</td>
              </tr>
            ))
          ) : (
            <tr><td colSpan={2}>Veri bulunamadı</td></tr>
          )}
        </tbody>
      </table>
      <button className="dashboardModalCloseButton" onClick={() => setShowModal(false)}>Kapat</button>
    </div>
  </div>
)}

    </div>
  );
};

export default Dashboard;
