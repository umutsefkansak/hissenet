import React, { useEffect, useState, useMemo } from "react";
import "./TransactionHistory.css";
import Pagination from "../../components/common/Pagination/Pagination";
import { orderApi } from "../../server/order";

import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

const DEFAULT_PAGE_SIZE = 10;

let __pdfFontLoaded = false;
let __pdfFontName = "helvetica"; 
const arrayBufferToBase64 = (buffer) => {
  let binary = "";
  const bytes = new Uint8Array(buffer);
  const chunk = 0x8000;
  for (let i = 0; i < bytes.length; i += chunk) {
    binary += String.fromCharCode.apply(null, bytes.subarray(i, i + chunk));
  }
  return btoa(binary);
};

const tryLoadFont = async (doc, fileName, fontName) => {
  const url = `${process.env.PUBLIC_URL}/fonts/${fileName}`;
  const res = await fetch(url);
  if (!res.ok) throw new Error(`Font not found: ${url}`);
  const buf = await res.arrayBuffer();
  const base64 = arrayBufferToBase64(buf);
  doc.addFileToVFS(fileName, base64);
  doc.addFont(fileName, fontName, "normal");
  return fontName;
};

const loadPdfFont = async (doc) => {
  if (__pdfFontLoaded) return __pdfFontName;

  try {
    __pdfFontName = await tryLoadFont(doc, "NotoSans-Regular.ttf", "NotoSans");
  } catch {
    try {
      __pdfFontName = await tryLoadFont(doc, "DejaVuSans.ttf", "DejaVuSans");
    } catch {
      __pdfFontName = "helvetica";
    }
  }
  __pdfFontLoaded = true;
  return __pdfFontName;
};

const TransactionHistory = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
  const [searchTerm, setSearchTerm] = useState("");

  const [typeFilter, setTypeFilter] = useState("ALL");

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const res = await orderApi.getAllOrders();
        const list = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [];
        setOrders(list);
      } catch (err) {
        console.error(err);
        setError("Emirler alınamadı.");
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, []);

  const getOrderTypeLabel = (type) => (type === "BUY" ? "Alış" : type === "SELL" ? "Satış" : type);
  const getOrderTypeClass  = (type) => (type === "BUY" ? "buy"  : type === "SELL" ? "sell"  : "");
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
  const getOrderCategoryLabel = (c) => (c === "MARKET" ? "Piyasa" : c === "LIMIT" ? "Limit" : c);
  const formatDate = (s) => (s ? new Date(s).toLocaleString("tr-TR") : "-");
  const formatBlockedBalance = (b) => {
    const n = Number(b || 0);
    if (!n) return "Yok";
    return <span className="blocked-balance">{n.toLocaleString("tr-TR")} ₺</span>;
  };

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

    if (typeFilter !== "ALL") {
      arr = arr.filter((o) => o.type === typeFilter);
    }

    return arr;
  }, [orders, searchTerm, typeFilter]);

  const totalElements = filteredOrders.length;
  const totalPages = Math.max(1, Math.ceil(totalElements / pageSize));

  useEffect(() => {
    if (page > totalPages - 1) setPage(0);
  }, [totalPages, page]);

  useEffect(() => {
    setPage(0);
  }, [typeFilter]);

  const start = page * pageSize;
  const end = start + pageSize;
  const paginatedOrders = filteredOrders.slice(start, end);

  const handlePageChange = (pageZeroBased) => setPage(pageZeroBased);
  const handlePageSizeChange = (newSize ) => { setPageSize(newSize); setPage(0); };
  const onChangeSearch = (e) => { setSearchTerm(e.target.value); setPage(0); };

  const exportToExcelCSV = () => {
    const data = filteredOrders;
    const headers = ["Tarih","Hisse","Adet","Fiyat","Toplam","İşlem Türü","İşlem Durumu","Blokaj","Emir Tipi"];

    const escape = (val) => {
      const s = (val ?? "").toString();
      const escaped = s.replace(/"/g, '""');
      return `"${escaped}"`;
    };

    const rows = data.map((o) => {
      const tarih = o.createdAt ? new Date(o.createdAt).toLocaleString("tr-TR") : "-";
      const hisse = o.stockCode ?? "";
      const adet = Number(o.quantity ?? 0).toLocaleString("tr-TR");
      const fiyat = Number(o.price ?? 0).toLocaleString("tr-TR", { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + " ₺";
      const toplam = Number(o.totalAmount ?? 0).toLocaleString("tr-TR") + " ₺";
      const islemTuru = getOrderTypeLabel(o.type);
      const islemDurumu = getOrderStatusLabel(o.status);
      const blokaj = Number(o.blockedBalance ?? 0) ? Number(o.blockedBalance).toLocaleString("tr-TR") + " ₺" : "Yok";
      const emirTipi = getOrderCategoryLabel(o.category);

      return [tarih,hisse,adet,fiyat,toplam,islemTuru,islemDurumu,blokaj,emirTipi].map(escape).join(",");
    });

    const csv = [headers.map(escape).join(","), ...rows].join("\r\n");
    const blob = new Blob(["\uFEFF", csv], { type: "text/csv;charset=utf-8;" });

    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    const now = new Date();
    const stamp = now.toISOString().replace(/[:.]/g, "-");
    a.download = `tum-islemler-${stamp}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  const exportToPDF = async () => {
    const doc = new jsPDF({ orientation: "landscape", unit: "pt", format: "a4" });

    const fontName = await loadPdfFont(doc); 
    doc.setFont(fontName, "normal");

    doc.setFontSize(14);
    doc.text("Tüm İşlemler", 40, 40);

    const head = [[
      "Tarih","Hisse","Adet","Fiyat","Toplam","İşlem Türü","İşlem Durumu","Blokaj","Emir Tipi",
    ]];

    const body = filteredOrders.map((o) => ([
      o.createdAt ? new Date(o.createdAt).toLocaleString("tr-TR") : "-",
      o.stockCode ?? "",
      Number(o.quantity ?? 0).toLocaleString("tr-TR"),
      Number(o.price ?? 0).toLocaleString("tr-TR", { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + " ₺",
      Number(o.totalAmount ?? 0).toLocaleString("tr-TR") + " ₺",
      getOrderTypeLabel(o.type),
      getOrderStatusLabel(o.status),
      Number(o.blockedBalance ?? 0) ? Number(o.blockedBalance).toLocaleString("tr-TR") + " ₺" : "Yok",
      getOrderCategoryLabel(o.category),
    ]));

    autoTable(doc, {
      head,
      body,
      startY: 60,
      styles: { font: fontName, fontSize: 9, cellPadding: 6, overflow: "linebreak" },
      headStyles: { font: fontName, fillColor: [232, 232, 232] },
      theme: "striped",
      didDrawPage: () => {
        const pageCount = doc.getNumberOfPages();
        const pageSize = doc.internal.pageSize;
        const pageWidth = pageSize.getWidth();
        const pageHeight = pageSize.getHeight();
        doc.setFontSize(9);
        doc.text(
          `Sayfa ${doc.internal.getCurrentPageInfo().pageNumber} / ${pageCount}`,
          pageWidth - 80,
          pageHeight - 20
        );
      },
    });

    const stamp = new Date().toISOString().replace(/[:.]/g, "-");
    doc.save(`tum-islemler-${stamp}.pdf`);
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
        <span className="transaction-count">Toplam: {totalElements} işlem</span>
      </div>

      <div style={{ display: "flex", gap: 12, alignItems: "center", marginBottom: 12 }}>
        <div style={{ display: "flex", gap: 8 }}>
          <button
            onClick={() => setTypeFilter("ALL")}
            style={{
              padding: "8px 10px",
              borderRadius: 6,
              border: "1px solid #d0d5dd",
              background: typeFilter === "ALL" ? "#e8eefc" : "#fff",
              color: "#111",
              cursor: "pointer",
              fontWeight: 600
            }}
          >
            Tümü
          </button>
          <button
            onClick={() => setTypeFilter("BUY")}
            style={{
              padding: "8px 10px",
              borderRadius: 6,
              border: "1px solid #d0d5dd",
              background: typeFilter === "BUY" ? "#e3f2fd" : "#fff",
              color: "#1976d2",
              cursor: "pointer",
              fontWeight: 600
            }}
          >
            Alış
          </button>
          <button
            onClick={() => setTypeFilter("SELL")}
            style={{
              padding: "8px 10px",
              borderRadius: 6,
              border: "1px solid #d0d5dd",
              background: typeFilter === "SELL" ? "#fff3cd" : "#fff",
              color: "#856404",
              cursor: "pointer",
              fontWeight: 600
            }}
          >
            Satış
          </button>
        </div>

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
            outline: "none",
          }}
        />

        <button
          onClick={exportToExcelCSV}
          style={{
            padding: "8px 12px",
            borderRadius: 6,
            border: "1px solid #0d6efd",
            background: "#e7f1ff",
            color: "#0d47a1",
            fontWeight: 600,
            cursor: "pointer",
            whiteSpace: "nowrap",
          }}
          title="Excel (CSV) olarak indir"
        >
          Excel’e Aktar
        </button>

        <button
          onClick={exportToPDF}
          style={{
            padding: "8px 12px",
            borderRadius: 6,
            border: "1px solid #dc3545",
            background: "#fdecec",
            color: "#a10000",
            fontWeight: 600,
            cursor: "pointer",
            whiteSpace: "nowrap",
          }}
          title="PDF olarak indir"
        >
          PDF’e Aktar
        </button>
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
                <td>{Number(order.quantity || 0).toLocaleString("tr-TR")}</td>
                <td>
                  {Number(order.price || 0).toLocaleString("tr-TR", {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })} ₺
                </td>
                <td>{Number(order.totalAmount || 0).toLocaleString("tr-TR")} ₺</td>
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
    </div>
  );
};

export default TransactionHistory;
