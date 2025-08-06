import React, { useEffect, useState } from "react";
import "./Dashboard.css";
import { orderApi } from "../../services/api/orderApi"; 

const Dashboard = () => {
  const [dailyVolume, setDailyVolume] = useState(null);
  const [recentOrders, setRecentOrders] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const volumeResponse = await orderApi.getTodayTotalTradeVolume();
        const recentResponse = await orderApi.getLastFiveOrders();

        setDailyVolume(volumeResponse.data); 
        setRecentOrders(recentResponse.data); 
      } catch (error) {
        console.error("Dashboard verileri alınamadı:", error);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="dashboard">
      <h1 className="title">Hoş Geldiniz</h1>

      <div className="summaryCards">
        <div className="card">
          <span className="cardTitle">BIST 100</span>
          <span className="cardValue">Günlük İşlem<br />147</span>
        </div>
        <div className="card">
          <span className="cardTitle">Günlük Toplam Hacim</span>
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
        <div className="popularStocks">
          <h3>Popüler Hisseler</h3>
          <table>
            <thead>
              <tr>
                <th>Hisseler</th>
                <th>Akrşel</th>
                <th>+ktl.*</th>
                <th>€</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>THYAO</td>
                <td>250,55 €</td>
                <td className="green">+3,12</td>
                <td className="green">+1,26%</td>
              </tr>
              <tr>
                <td>TTKOM</td>
                <td>28,84 €</td>
                <td className="green">+0,47</td>
                <td className="green">+1,65%</td>
              </tr>
              <tr>
                <td>BIST 100</td>
                <td>9.845 €</td>
                <td className="red">-1,8%</td>
                <td className="red">-1,37%</td>
              </tr>
              <tr>
                <td>ARCLK</td>
                <td>14,15 €</td>
                <td className="red">-0,111</td>
                <td className="red">-0,76%</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div className="recentTransactions">
          <div className="transactionsHeader">
            <h3>Son İşlemler</h3>
          </div>
          <ul>
            {recentOrders.length > 0 ? (
              recentOrders.map((order, index) => (
                <li key={index}>
                    <strong>{order.stockCode}</strong> – {order.totalAmount} ₺ ({order.type === 'BUY' ? 'Alış' : 'Satış'})
                </li>
              ))
            ) : (
              <li>Yükleniyor...</li>
            )}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
