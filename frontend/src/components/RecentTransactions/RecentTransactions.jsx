import React, { useEffect, useState } from "react";
import "./RecentTransactions.css";
import { orderApi } from "../../server/order";
import { customerApi } from "../../server/customerApi";

const RecentTransactions = () => {
  const [recentOrders, setRecentOrders] = useState([]);
  const [allFilledOrders, setAllFilledOrders] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [customerNames, setCustomerNames] = useState({});

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const recentResponse = await orderApi.getAllOrders();
        setRecentOrders(recentResponse.data.slice(0, 8));
      } catch (error) {
        console.error("Son işlemler getirilemedi:", error);
      }
    };
    fetchOrders();
  }, []);

  useEffect(() => {
    const fetchCustomerNames = async () => {
      const allOrders = [...recentOrders, ...allFilledOrders];
      const uniqueIds = [...new Set(allOrders.map((o) => o.customerId))];
      const nameMap = { ...customerNames };

      await Promise.all(
        uniqueIds.map(async (id) => {
          if (!nameMap[id]) {
            try {
              const customer = await customerApi.getCustomerById(id);
              const c = customer?.data ?? customer;
              if (c.isDeleted) {
                nameMap[id] = "Silinmiş Müşteri";
                return;
              }
              nameMap[id] =
                c.customerType === "INDIVIDUAL"
                  ? `${c.firstName ?? ""} ${c.lastName ?? ""}`.trim()
                  : c.companyName ?? "Kurumsal";
            } catch (err) {
              nameMap[id] = "Hata";
              console.error(`Müşteri verisi alınamadı (id=${id}):`, err);
            }
          }
        })
      );
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

  const renderRow = (order) => {
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
    <div className="recentTransactions">
      <div className="transactionsHeader">
        <h3>Son İşlemler</h3>
        <button className="viewAll" onClick={handleShowModal}>
          Tümünü Gör
        </button>
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
            recentOrders.map(renderRow)
          ) : (
            <tr>
              <td colSpan={4}>Yükleniyor...</td>
            </tr>
          )}
        </tbody>
      </table>

      {showModal && (
        <div
          className="dashboardModalOverlay"
          onClick={() => setShowModal(false)}
        >
          <div
            className="dashboardModalContent"
            onClick={(e) => e.stopPropagation()}
          >
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
                  allFilledOrders.map(renderRow)
                ) : (
                  <tr>
                    <td colSpan={4}>Veri bulunamadı</td>
                  </tr>
                )}
              </tbody>
            </table>
            <button
              className="dashboardModalCloseButton"
              onClick={() => setShowModal(false)}
            >
              Kapat
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default RecentTransactions;
