import React, { useState, useEffect } from 'react';
import { orderApi } from '../../server/order';
import { customerApi } from '../../server/customerApi';
import './Reports.css';

const Reports = () => {
  const [activeTab, setActiveTab] = useState('daily');
  const [orders, setOrders] = useState([]);
  const [customers, setCustomers] = useState({}); // customerId -> customer data mapping
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchTodayOrders();
  }, []);

  const fetchTodayOrders = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await orderApi.getTodayFilledOrders();
      
      if (response.status === 200 && response.data) {
        setOrders(response.data);
        // Fetch customer names for all unique customer IDs
        await fetchCustomerNames(response.data);
      }
    } catch (err) {
      console.error('Error fetching today orders:', err);
      setError('Bugünkü işlemler yüklenirken hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const fetchCustomerNames = async (ordersData) => {
    try {
      // Get unique customer IDs
      const uniqueCustomerIds = [...new Set(ordersData.map(order => order.customerId))];
      
      // Fetch customer data for each unique ID
      const customerPromises = uniqueCustomerIds.map(async (customerId) => {
        try {
          const response = await customerApi.getCustomerById(customerId);
          return { id: customerId, data: response.data };
        } catch (err) {
          console.error(`Error fetching customer ${customerId}:`, err);
          return { id: customerId, data: null };
        }
      });

      const customerResults = await Promise.all(customerPromises);
      
      // Create a mapping of customerId -> customer data
      const customerMap = {};
      customerResults.forEach(result => {
        if (result.data) {
          customerMap[result.id] = result.data;
        }
      });
      
      setCustomers(customerMap);
    } catch (err) {
      console.error('Error fetching customer names:', err);
    }
  };

  const getCustomerName = (customerId) => {
    const customer = customers[customerId];
    if (customer) {
      // Check if it's individual or corporate customer
      if (customer.firstName && customer.lastName) {
        return `${customer.firstName} ${customer.lastName}`;
      } else if (customer.companyName) {
        return customer.companyName;
      }
    }
    return `Müşteri ${customerId}`;
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(amount);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('tr-TR');
  };

  const getTodayFormatted = () => {
    const today = new Date();
    return today.toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      weekday: 'long'
    });
  };

  const getOrderTypeText = (type) => {
    return type === 'BUY' ? 'Alış' : type === 'SELL' ? 'Satış' : type;
  };

  const getCategoryText = (category) => {
    return category === 'MARKET' ? 'Piyasa' : category === 'LIMIT' ? 'Limit' : category;
  };

  const generatePDF = async () => {
    try {
      // Dynamic import for jsPDF
      const { jsPDF } = await import('jspdf');
      
      const doc = new jsPDF();
      
      // Add title
      doc.setFontSize(20);
      doc.setFont('helvetica', 'bold');
      doc.text('Bugünkü İşlem Raporu', 20, 20);
      
      // Add date
      doc.setFontSize(12);
      doc.setFont('helvetica', 'normal');
      doc.text(getTodayFormatted(), 20, 30);
      
      // Add summary
      doc.setFontSize(14);
      doc.setFont('helvetica', 'bold');
      doc.text('Özet Bilgiler:', 20, 45);
      
      doc.setFontSize(10);
      doc.setFont('helvetica', 'normal');
      doc.text(`Toplam İşlem Sayısı: ${orders.length}`, 20, 55);
      doc.text(`Toplam İşlem Hacmi: ${formatCurrency(orders.reduce((sum, order) => sum + order.totalAmount, 0))}`, 20, 65);
      
      // Add table
      doc.setFontSize(12);
      doc.setFont('helvetica', 'bold');
      doc.text('İşlem Detayları:', 20, 85);
      
      // Table headers
      const headers = ['Tarih', 'Müşteri', 'Hisse', 'İşlem', 'Adet', 'Tutar'];
      const startY = 95;
      const colWidth = 30;
      
      headers.forEach((header, index) => {
        doc.setFontSize(10);
        doc.setFont('helvetica', 'bold');
        doc.text(header, 20 + (index * colWidth), startY);
      });
      
      // Table data
      let currentY = startY + 10;
      orders.forEach((order, index) => {
        if (currentY > 250) {
          doc.addPage();
          currentY = 20;
        }
        
        doc.setFontSize(8);
        doc.setFont('helvetica', 'normal');
        doc.text(formatDate(order.createdAt), 20, currentY);
        doc.text(getCustomerName(order.customerId), 50, currentY);
        doc.text(order.stockCode, 80, currentY);
        doc.text(getOrderTypeText(order.type), 110, currentY);
        doc.text(order.quantity.toString(), 140, currentY);
        doc.text(formatCurrency(order.totalAmount), 170, currentY);
        
        currentY += 8;
      });
      
      // Save PDF
      const fileName = `islem_raporu_${new Date().toISOString().split('T')[0]}.pdf`;
      doc.save(fileName);
      
    } catch (error) {
      console.error('PDF oluşturulurken hata:', error);
      alert('PDF oluşturulamadı. Lütfen tekrar deneyin.');
    }
  };

  const generateExcel = async () => {
    try {
      // Dynamic import for xlsx
      const XLSX = await import('xlsx');
      
      // Prepare data for Excel
      const excelData = orders.map(order => ({
        'Tarih': formatDate(order.createdAt),
        'Müşteri': getCustomerName(order.customerId),
        'Hisse': order.stockCode,
        'İşlem Türü': getOrderTypeText(order.type),
        'Adet': order.quantity,
        'Toplam Tutar': order.totalAmount,
        'Fiyat': order.price,
        'Komisyon': order.commission || 0,
        'Durum': order.status
      }));
      
      // Add summary row at the beginning
      const summaryRow = {
        'Tarih': 'ÖZET',
        'Müşteri': '',
        'Hisse': '',
        'İşlem Türü': '',
        'Adet': orders.length,
        'Toplam Tutar': orders.reduce((sum, order) => sum + order.totalAmount, 0),
        'Fiyat': '',
        'Komisyon': '',
        'Durum': ''
      };
      
      const allData = [summaryRow, ...excelData];
      
      // Create workbook and worksheet
      const wb = XLSX.utils.book_new();
      const ws = XLSX.utils.json_to_sheet(allData);
      
      // Set column widths
      const colWidths = [
        { wch: 15 }, // Tarih
        { wch: 25 }, // Müşteri
        { wch: 10 }, // Hisse
        { wch: 12 }, // İşlem Türü
        { wch: 10 }, // Adet
        { wch: 15 }, // Toplam Tutar
        { wch: 12 }, // Fiyat
        { wch: 12 }, // Komisyon
        { wch: 12 }  // Durum
      ];
      ws['!cols'] = colWidths;
      
      // Add worksheet to workbook
      XLSX.utils.book_append_sheet(wb, ws, 'İşlem Raporu');
      
      // Save Excel file
      const fileName = `islem_raporu_${new Date().toISOString().split('T')[0]}.xlsx`;
      XLSX.writeFile(wb, fileName);
      
    } catch (error) {
      console.error('Excel oluşturulurken hata:', error);
      alert('Excel dosyası oluşturulamadı. Lütfen tekrar deneyin.');
    }
  };

  if (loading) {
    return (
      <div className="reports">
        <div className="loading">Rapor yükleniyor...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="reports">
        <div className="error">{error}</div>
        <button onClick={fetchTodayOrders} className="retry-btn">Tekrar Dene</button>
      </div>
    );
  }

  return (
    <div className="reports">
      {/* Top Navigation Tabs */}
      <div className="report-tabs">
        <button 
          className={`tab-button ${activeTab === 'daily' ? 'active' : ''}`}
          onClick={() => setActiveTab('daily')}
        >
          Günlük İşlem Raporu
        </button>
        <button 
          className={`tab-button ${activeTab === 'customer' ? 'active' : ''}`}
          onClick={() => setActiveTab('customer')}
        >
          Müşteri Bazlı İşlem Raporu
        </button>
        <button 
          className={`tab-button ${activeTab === 'portfolio' ? 'active' : ''}`}
          onClick={() => setActiveTab('portfolio')}
        >
          Portföy Değer Raporu
        </button>
        <button 
          className={`tab-button ${activeTab === 'popular' ? 'active' : ''}`}
          onClick={() => setActiveTab('popular')}
        >
          En Çok İşlem Gören Hisseler
        </button>
      </div>

      {/* Summary Cards */}
      <div className="summary-cards">
        <div className="summary-card">
          <h3>Bugünkü İşlem Hacmi</h3>
          <p className="card-value">₺ 3.250.000</p>
        </div>
        <div className="summary-card">
          <h3>Yeni Müşteri Sayısı</h3>
          <p className="card-value">5</p>
        </div>
        <div className="summary-card">
          <h3>En Çok İşlem Gören Hisse</h3>
          <p className="card-value negative">▼ 1,2 %</p>
        </div>
      </div>

      {/* Report Title and Date */}
      <div className="report-header">
        <div className="header-left">
          <h2>Bugünkü İşlem Raporu</h2>
          <p className="report-date">{getTodayFormatted()}</p>
        </div>
        <div className="header-right">
          <button className="download-btn" onClick={generatePDF}>İndir ></button>
          <button className="download-btn" onClick={generateExcel}>Excel İndir ></button>
        </div>
      </div>

      {/* Main Transaction Table */}
      <div className="transaction-table">
        <table>
          <thead>
            <tr>
              <th>Tarih</th>
              <th>Kullanıcı</th>
              <th>Hisse</th>
              <th>Emir Türü</th>
              <th>Adet</th>
              <th>Toplam Tutar</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id}>
                <td>{formatDate(order.createdAt)}</td>
                <td>{getCustomerName(order.customerId)}</td>
                <td>{order.stockCode}</td>
                <td>{getOrderTypeText(order.type)}</td>
                <td>{order.quantity.toLocaleString()}</td>
                <td>{formatCurrency(order.totalAmount)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Secondary Section */}
      <div className="secondary-section">
        <h3>Günlük Güncelleme</h3>
        <div className="transaction-table">
          <table>
            <thead>
              <tr>
                <th>Tarih</th>
                <th>Kullanıcı</th>
                <th>Hisse</th>
                <th>Emir Türü</th>
                <th>Adet</th>
                <th>Toplam Tutar</th>
              </tr>
            </thead>
            <tbody>
              {orders.slice(0, 5).map((order) => (
                <tr key={`secondary-${order.id}`}>
                  <td>{formatDate(order.createdAt)}</td>
                  <td>{getCustomerName(order.customerId)}</td>
                  <td>{order.stockCode}</td>
                  <td>{getOrderTypeText(order.type)}</td>
                  <td>{order.quantity.toLocaleString()}</td>
                  <td>{formatCurrency(order.totalAmount)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Reports; 