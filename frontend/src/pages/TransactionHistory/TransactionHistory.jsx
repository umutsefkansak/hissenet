import React, { useEffect, useState, useMemo } from "react";
import "./TransactionHistory.css";
import Pagination from "../../components/common/Pagination/Pagination";
import ExportMenu from "../../components/common/Export/ExportMenu";
import SortableHeader from "../../components/common/Sorting/SortableHeader";
import { orderApi } from "../../server/order";
import { sortList } from "../../components/common/Sorting/sortUtils";
import Modal from "../../components/common/Modal/Modal";

const DEFAULT_PAGE_SIZE = 5;

const TransactionHistory = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
  const [searchTerm, setSearchTerm] = useState("");
  const [sortConfig, setSortConfig] = useState({ key: "createdAt", direction: "desc" });
  const [showCancelModal, setShowCancelModal] = useState(false);
  const [targetOrder, setTargetOrder] = useState(null);
  const [isCancelling, setIsCancelling] = useState(false);
  const [scopedCustomerId, setScopedCustomerId] = useState(null);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        const cidRaw = localStorage.getItem("customerId");
        const cid = cidRaw ? cidRaw.toString().trim() : null;
        setScopedCustomerId(cid || null);
        const res = await orderApi.getAllOrders();
        const all = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [];
        let list = all;
        if (cid) {
          list = all.filter(o => {
            const oid = o?.customerId ?? o?.customerID ?? o?.customer_id ?? o?.customer?.id;
            return String(oid ?? "") === String(cid);
          });
        }
        setOrders(list);
      } catch (err) {
        setError("Emirler alınamadı.");
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, []);

  const getOrderTypeLabel = (type) => (type === "BUY" ? "Alış" : type === "SELL" ? "Satış" : type);
  const getOrderTypeClass = (type) => (type === "BUY" ? "buy" : type === "SELL" ? "sell" : "");
  const getOrderStatusLabel = (status) => {
    switch (status) {
      case "OPEN": return "Açık";
      case "FILLED": return "Gerçekleşti";
      case "CANCELED":
      case "CANCELLED": return "İptal Edildi";
      case "REJECTED": return "Reddedildi";
      case "FAILED": return "Başarısız";
      default: return status;
    }
  };
  const getOrderStatusClass = (status) => {
    switch (status) {
      case "FILLED": return "completed";
      case "OPEN": return "pending";
      case "CANCELED":
      case "CANCELLED": return "canceled";
      case "REJECTED": return "rejected";
      case "FAILED": return "failed";
      default: return "";
    }
  };
  const getOrderCategoryLabel = (c) => (c === "MARKET" ? "Piyasa" : c === "LIMIT" ? "Limit" : c);
  const formatDate = (s) => (s ? new Date(s).toLocaleString("tr-TR") : "-");

  const normalized = (v) => (v ?? "").toString().toLowerCase().trim();

  const filteredOrders = useMemo(() => {
    const q = normalized(searchTerm);
    let arr = orders;
    if (q) {
      arr = arr.filter((o) => {
        const code = normalized(o.stockCode);
        const typeLabel = normalized(getOrderTypeLabel(o.type));
        const statusLabel = normalized(getOrderStatusLabel(o.status));
        const categoryLabel = normalized(getOrderCategoryLabel(o.category));
        const typeRaw = normalized(o.type);
        const statusRaw = normalized(o.status);
        const categoryRaw = normalized(o.category);
        const price = normalized(Number(o.price) ? Number(o.price).toLocaleString("tr-TR") : "");
        const total = normalized(Number(o.totalAmount) ? Number(o.totalAmount).toLocaleString("tr-TR") : "");
        return (
          code.includes(q) ||
          typeLabel.includes(q) || typeRaw.includes(q) ||
          statusLabel.includes(q) || statusRaw.includes(q) ||
          categoryLabel.includes(q) || categoryRaw.includes(q) ||
          price.includes(q) || total.includes(q)
        );
      });
    }
    arr = sortList(arr, sortConfig.key, sortConfig.direction);
    return arr;
  }, [orders, searchTerm, sortConfig]);

  const totalElements = filteredOrders.length;
  const totalPages = Math.max(1, Math.ceil(totalElements / pageSize));

  useEffect(() => {
    if (page > totalPages - 1) setPage(0);
  }, [totalPages, page]);

  const start = page * pageSize;
  const end = start + pageSize;
  const paginatedOrders = filteredOrders.slice(start, end);

  const handlePageChange = (pageZeroBased) => setPage(pageZeroBased);
  const handlePageSizeChange = (newSize) => { setPageSize(newSize); setPage(0); };
  const onChangeSearch = (e) => { setSearchTerm(e.target.value); setPage(0); };
  const handleSort = (key) => {
    setSortConfig(prev => ({
      key,
      direction: prev.key === key && prev.direction === "asc" ? "desc" : "asc"
    }));
    setPage(0);
  };

  const openCancelModal = (order) => {
    setTargetOrder(order);
    setShowCancelModal(true);
  };
  const closeCancelModal = () => {
    if (isCancelling) return;
    setShowCancelModal(false);
    setTargetOrder(null);
  };
  const confirmCancel = async () => {
    if (!targetOrder) return;
    try {
      setIsCancelling(true);
      await orderApi.updateOrder(targetOrder.id, { status: "CANCELED" });
      setOrders(prev => prev.map(o => (o.id === targetOrder.id ? { ...o, status: "CANCELED" } : o)));
      if (window?.showToast) window.showToast("Emir iptal edildi", "success", 2500);
      closeCancelModal();
    } catch (err) {
      if (window?.showToast) window.showToast(err.message || "İptal işlemi başarısız", "error", 3500);
    } finally {
      setIsCancelling(false);
    }
  };

  const fmtDateForPdf = (v) => {
    if (!v) return "-";
    const d = new Date(v);
    return d.toLocaleDateString("tr-TR") + " " + d.toLocaleTimeString("tr-TR", { hour: "2-digit", minute: "2-digit" });
  };
  const fmtQtyPdf = (v) => Number(v || 0).toLocaleString("tr-TR");
  const fmtMoneyPdf = (v) => Number(v || 0).toLocaleString("tr-TR", { minimumFractionDigits: 2, maximumFractionDigits: 2 });

  const exportColumns = [
    { key: "createdAt", label: "Tarih", formatter: fmtDateForPdf },
    { key: "stockCode", label: "Hisse" },
    { key: "quantity", label: "Adet", formatter: fmtQtyPdf },
    { key: "price", label: "Fiyat", formatter: fmtMoneyPdf },
    { key: "totalAmount", label: "Toplam", formatter: fmtMoneyPdf },
    { key: "status", label: "Durum", formatter: (v) => getOrderStatusLabel(v) }
  ];

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
        <h2>{scopedCustomerId ? "Müşterinin İşlemleri" : "Tüm İşlemler"}</h2>
        <span className="transaction-count">Toplam: {totalElements} işlem</span>
      </div>

      <div style={{ display: "flex", gap: 12, alignItems: "center", marginBottom: 12 }}>
        <input
          type="text"
          value={searchTerm}
          onChange={onChangeSearch}
          placeholder="Arama yapabilirsiniz."
          style={{
            flex: 1,
            padding: "8px 10px",
            borderRadius: 6,
            border: "1px solid #d0d5dd",
            background: "#fff",
            color: "#111",
            outline: "none"
          }}
        />
        <ExportMenu
          data={filteredOrders}
          columns={exportColumns}
          filename={scopedCustomerId ? `islemler-${scopedCustomerId}` : "tum-islemler"}
          pdfTitle={scopedCustomerId ? "Müşterinin İşlemleri" : "Tüm İşlemler"}
          pdfSubtitle={scopedCustomerId ? `Müşteri ID: ${scopedCustomerId} • Toplam ${totalElements} işlem` : `Toplam ${totalElements} işlem`}
        />
      </div>

      <div className="transaction-table-container">
        <table className="transaction-table">
          <thead>
            <tr>
              <SortableHeader columnKey="createdAt" label="Tarih" sortConfig={sortConfig} onSort={handleSort} />
              <SortableHeader columnKey="stockCode" label="Hisse" sortConfig={sortConfig} onSort={handleSort} />
              <SortableHeader columnKey="quantity" label="Adet" sortConfig={sortConfig} onSort={handleSort} />
              <SortableHeader columnKey="price" label="Fiyat" sortConfig={sortConfig} onSort={handleSort} />
              <SortableHeader columnKey="totalAmount" label="Toplam" sortConfig={sortConfig} onSort={handleSort} />
              <SortableHeader columnKey="status" label="İşlem Durumu" sortConfig={sortConfig} onSort={handleSort} />
              <th className="action-col">İşlem</th>
            </tr>
          </thead>
          <tbody>
            {paginatedOrders.map((order) => (
              <tr key={order.id}>
                <td>{formatDate(order.createdAt)}</td>
                <td>{order.stockCode}</td>
                <td>{Number(order.quantity || 0).toLocaleString("tr-TR")}</td>
                <td>{Number(order.price || 0).toLocaleString("tr-TR", { minimumFractionDigits: 2, maximumFractionDigits: 2 })} ₺</td>
                <td>{Number(order.totalAmount || 0).toLocaleString("tr-TR")} ₺</td>
                <td>
                  <span className={`transaction-status ${getOrderStatusClass(order.status)}`}>
                    {getOrderStatusLabel(order.status)}
                  </span>
                </td>
                <td className="action-cell">
                  {order.status === "OPEN" ? (
                    <button className="table-action-btn" onClick={() => openCancelModal(order)}>
                      İptal Et
                    </button>
                  ) : null}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div style={{ marginTop: 12 }}>
        <Pagination
          currentPage={page}
          totalPages={totalPages}
          totalElements={totalElements}
          pageSize={pageSize}
          onPageChange={handlePageChange}
          onPageSizeChange={handlePageSizeChange}
          showPageSizeOptions={true}
          pageSizeOptions={[5, 10, 20, 50]}
          showTotalElements={true}
        />
      </div>

      {showCancelModal && targetOrder && (
        <Modal
          variant="confirm"
          title="Emri iptal et?"
          message={
            <>
              <p><strong>Hisse:</strong> {targetOrder.stockCode}</p>
              <p>
                <strong>Adet/Fiyat:</strong>{" "}
                {Number(targetOrder.quantity || 0).toLocaleString("tr-TR")} /{" "}
                {Number(targetOrder.price || 0).toLocaleString("tr-TR", { minimumFractionDigits: 2, maximumFractionDigits: 2 })} ₺
              </p>
              <p><strong>Toplam:</strong> {Number(targetOrder.totalAmount || 0).toLocaleString("tr-TR")} ₺</p>
              <p style={{ marginTop: 8 }}>Bu emri iptal etmek istediğinize emin misiniz?</p>
            </>
          }
          confirmText={isCancelling ? "İptal ediliyor..." : "Evet, iptal et"}
          cancelText="Vazgeç"
          onConfirm={isCancelling ? undefined : confirmCancel}
          onClose={closeCancelModal}
        />
      )}
    </div>
  );
};

export default TransactionHistory;
