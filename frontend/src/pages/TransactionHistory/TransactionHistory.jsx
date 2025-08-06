import React, { useEffect, useState } from "react";
import { orderApi } from "../../services/api/orderApi";
import "./TransactionHistory.css";

const ITEMS_PER_PAGE = 10;

const TransactionHistory = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await orderApi.getAllOrders();
        setOrders(response.data);
        setLoading(false);
      } catch (err) {
        setError("Emirler alınamadı.");
        setLoading(false);
      }
    };
    fetchOrders();
  }, []);

  const getOrderTypeLabel = (type) => type === "BUY" ? "Alış" : type === "SELL" ? "Satış" : type;
  const getOrderTypeClass = (type) => type === "BUY" ? "buy" : type === "SELL" ? "sell" : "";

  const getOrderStatusLabel = (status) => {
    switch (status) {
      case "OPEN": return "Açık";
      case "FILLED": return "Gerçekleşti";
      case "CANCELED": return "İptal Edildi";
      case "REJECTED": return "Reddedildi";
      case "FAILED": return "Başarısız";
      default: return status;
    }
  };
  const getOrderStatusClass = (status) => {
    switch (status) {
      case "FILLED": return "completed";
      case "OPEN": return "pending";
      case "REJECTED": return "rejected";
      case "FAILED": return "failed";
      default: return "";
    }
  };

  const getOrderCategoryLabel = (category) => category === "MARKET" ? "Piyasa" : category === "LIMIT" ? "Limit" : category;
  const formatDate = (dateString) => new Date(dateString).toLocaleString("tr-TR");

  const formatBlockedBalance = (balance) => {
    if (!balance || Number(balance) === 0) return "Yok";
    return <span className="blocked-balance">{Number(balance).toLocaleString()} ₺</span>;
  };

  const totalPages = Math.ceil(orders.length / ITEMS_PER_PAGE);
  const paginatedOrders = orders.slice((currentPage - 1) * ITEMS_PER_PAGE, currentPage * ITEMS_PER_PAGE);

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  if (loading) {
    return (
      <div className="transaction-history-loading">
        <div className="loading-spinner"></div>
        <p>İşlemler yükleniyor...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="transaction-history-error">
        <p>{error}</p>
        <button onClick={() => window.location.reload()}>Yeniden Dene</button>
      </div>
    );
  }

  return (
    <div className="transaction-history">
      <div className="transaction-history-header">
        <h2>Tüm İşlemler</h2>
        <span className="transaction-count">Toplam: {orders.length} işlem</span>
      </div>

      <div className="transaction-table-container">
        <table className="transaction-table">
          <thead>
            <tr>
              <th>Tarih</th>
              <th>Hisse</th>
              <th>Adet</th>
              <th>Fiyat</th>
              <th>Toplam</th>
              <th>İşlem Türü</th>
              <th>İşlem Durumu</th>
              <th>Blokaj</th>
              <th>Emir Tipi</th>
            </tr>
          </thead>
          <tbody>
            {paginatedOrders.map((order) => (
              <tr key={order.id}>
                <td>{formatDate(order.createdAt)}</td>
                <td>{order.stockCode}</td>
                <td>{Number(order.quantity).toLocaleString()}</td>
                <td>{Number(order.price).toFixed(2)} ₺</td>
                <td>{Number(order.totalAmount).toLocaleString()} ₺</td>
                <td>
                  <span className={`transaction-type ${getOrderTypeClass(order.type)}`}>
                    {getOrderTypeLabel(order.type)}
                  </span>
                </td>
                <td>
                  <span className={`transaction-status ${getOrderStatusClass(order.status)}`}>
                    {getOrderStatusLabel(order.status)}
                  </span>
                </td>
                <td>{formatBlockedBalance(order.blockedBalance)}</td>
                <td>{getOrderCategoryLabel(order.category)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="pagination">
        <button onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 1}>
          Önceki
        </button>
        <span>Sayfa {currentPage} / {totalPages}</span>
        <button onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === totalPages}>
          Sonraki
        </button>
      </div>
    </div>
  );
};

export default TransactionHistory;
