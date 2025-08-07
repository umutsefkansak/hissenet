import React, { useEffect, useState } from "react";
import "./Dashboard.css";
import PopularStocks from "../../components/PopularStocks/PopularStocks";
import Bist100Card from "../../components/Bist100/Bist100";
import { orderApi } from "../../server/order";
import { customerApi } from "../../server/customerApi";

const Dashboard = () => {
  const [dailyVolume, setDailyVolume] = useState(null);
  const [todayOrderCount, setTodayOrderCount] = useState(null);
  const [recentOrders, setRecentOrders] = useState([]);
  const [allFilledOrders, setAllFilledOrders] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [customerNames, setCustomerNames] = useState({});

  useEffect(() => {
    const fetchData = async () => {
      try {
        const volumeResponse = await orderApi.getTotalTradeVolume();
        const countResponse = await orderApi.getTodayOrderCount();
        const recentResponse = await orderApi.getAllOrders();

        setDailyVolume(volumeResponse.data);
        setTodayOrderCount(countResponse.data);
        setRecentOrders(recentResponse.data.slice(0, 9));
      } catch (error) {
        console.error("Dashboard verileri alınamadı:", error);
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    const fetchCustomerNames = async () => {
      const allOrders = [...recentOrders, ...allFilledOrders];
      const uniqueIds = [...new Set(allOrders.map(o => o.customerId))];

      const nameMap = { ...customerNames };
      await Promise.all(uniqueIds.map(async (id) => {
        if (!nameMap[id]) {
          try {
            const customer = await customerApi.getCustomerById(id);
            const c = customer?.data ?? customer;

            if (c.isDeleted === true || c.isDeleted === 1) {
              nameMap[id] = "Silinmiş Müşteri";
              return;
            }

            const name = c.customerType === "INDIVIDUAL"
              ? `${c.firstName ?? ""} ${c.lastName ?? ""}`.trim()
              : c.companyName ?? "Kurumsal";

            nameMap[id] = name || "Bilinmeyen";
          } catch (err) {
            nameMap[id] = "Hata";
            console.error(`Müşteri alınamadı (id=${id}):`, err);
          }
        }
      }));

      setCustomerNames(nameMap);
    };

    if (recentOrders.length > 0 || allFilledOrders.length > 0) {
      fetchCustomerNames();
    }
  }, [recentOrders, allFilledOrders]);

  const handleShowModal = async () => {
    try {
      const response = await orderApi.getTodayFilledOrders();
      setAllFilledOrders(response.data);
      setShowModal(true);
    } catch (error) {
      console.error("Tüm işlemler getirilemedi:", error);
    }
  };

  const renderOrderRow = (order) => {
    const name = customerNames[order.customerId] || "Yükleniyor...";
    const isBuy = order.type === "BUY";
    const typeLabel = isBuy ? "ALIŞ" : "SATIŞ";
    const formattedAmount = Number(order.totalAmount).toLocaleString() + " TL";

    return (
      <tr key={order.id}>
        <td>{name}</td>
        <td>{order.stockCode}</td>
        <td className={isBuy ? "green" : "red"}>{typeLabel}</td>
        <td className="right">{formattedAmount}</td>
      </tr>
    );
  };

  return (
    <div className="dashboard">
      <h1 className="title">Hoş Geldiniz</h1>

      <div className="summaryCards">
        <Bist100Card className="card"/>

        <div className="card">
          <span className="cardTitle">Günlük İşlem</span>
          <span className="cardValue">
            {todayOrderCount !== null ? todayOrderCount : 'Yükleniyor...'}
          </span>
        </div>

        <div className="card">
          <span className="cardTitle">Toplam Hacim</span>
          <span className="cardValue">
            {dailyVolume !== null ? `${dailyVolume}₺` : 'Yükleniyor...'}
          </span>
        </div>

        <div className="card">
          <span className="cardTitle">Aktif Müşteri</span>
          <span className="cardValue">1.000</span>
        </div>
      </div>

      <div className="bottomSection">
        <PopularStocks className="noMaxWidth" />
        <div className="recentTransactions">
          <div className="transactionsHeader">
            <h3>Son İşlemler</h3>
            <button className="viewAll" onClick={handleShowModal}>Tümünü Gör</button>
          </div>
          <table className="transactionsTable">
            <thead>
              <tr>
                <th>Müşteri</th>
                <th>Hisse</th>
                <th>İşlem</th>
                <th>Tutar</th>
              </tr>
            </thead>
            <tbody>
              {recentOrders.length > 0 ? (
                recentOrders.map(renderOrderRow)
              ) : (
                <tr><td colSpan={4}>Yükleniyor...</td></tr>
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
              <thead>
                <tr>
                  <th>Müşteri</th>
                  <th>Hisse</th>
                  <th>İşlem</th>
                  <th>Tutar</th>
                </tr>
              </thead>
              <tbody>
                {allFilledOrders.length > 0 ? (
                  allFilledOrders.map(renderOrderRow)
                ) : (
                  <tr><td colSpan={4}>Veri bulunamadı</td></tr>
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
