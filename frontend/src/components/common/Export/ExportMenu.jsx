import React, { useState, useRef, useEffect } from 'react';
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import { saveAs } from 'file-saver';
import './ExportMenu.css';

const ExportMenu = ({
  data = [],
  columns = [], // [{ key, label, formatter?: (value, row) => string }]
  filename = 'export',
  pdfTitle = 'Export',
  pdfSubtitle = '',
  fontPath = '/fonts/NotoSans-Regular.ttf',
  className = ''
}) => {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    const onClickOutside = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener('mousedown', onClickOutside);
    return () => document.removeEventListener('mousedown', onClickOutside);
  }, []);

  const formatCell = (col, row) => {
    const raw = row[col.key];
    return col.formatter ? col.formatter(raw, row) : raw ?? '';
  };

  const handleExportExcel = () => {
    if (!data.length || !columns.length) return;

    const rows = data.map((row) => {
      const obj = {};
      columns.forEach((col) => {
        obj[col.label] = formatCell(col, row);
      });
      return obj;
    });

    const worksheet = XLSX.utils.json_to_sheet(rows);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Veriler');
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const file = new Blob([excelBuffer], { type: 'application/octet-stream' });
    saveAs(file, `${filename}.xlsx`);
    setOpen(false);
  };

  const handleExportPDF = () => {
    if (!data.length || !columns.length) return;

    const pdf = new jsPDF();

    try {
      pdf.addFont(fontPath, 'NotoSans', 'normal');
      pdf.setFont('NotoSans');
    } catch (_) {
      // font yüklenemezse varsayılan font ile devam
    }

    // Başlıklar
    pdf.setFontSize(18);
    pdf.setTextColor(30, 55, 72);
    pdf.text(pdfTitle, 20, 30);

    if (pdfSubtitle) {
      pdf.setFontSize(12);
      pdf.setTextColor(74, 85, 104);
      pdf.text(pdfSubtitle, 20, 45);
    }

    const startY = pdfSubtitle ? 75 : 65;
    let currentY = startY;

    // Tablo başlıkları
    pdf.setFontSize(10);
    pdf.setTextColor(255, 255, 255);
    pdf.setFillColor(30, 58, 138);

    const leftX = 20;
    const usableWidth = 170; // 210 - 2*20 margin
    const colWidth = usableWidth / columns.length;

    columns.forEach((col, index) => {
      const x = leftX + index * colWidth;
      pdf.rect(x, currentY - 8, colWidth, 8, 'F');
      pdf.text(String(col.label), x + 2, currentY - 2);
    });

    currentY += 8;
    pdf.setTextColor(45, 55, 72);

    data.forEach((row) => {
      if (currentY > 250) {
        pdf.addPage();
        currentY = 20;
      }
      columns.forEach((col, index) => {
        const x = leftX + index * colWidth;
        const text = String(formatCell(col, row));
        pdf.text(text, x + 2, currentY);
      });
      currentY += 6;
    });

    pdf.save(`${filename}.pdf`);
    setOpen(false);
  };

  return (
    <div className={`export-dropdown ${className}`} ref={ref}>
      <button className="export-icon-button" onClick={() => setOpen((o) => !o)}>
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
             stroke="currentColor" strokeWidth="2">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
          <polyline points="7,10 12,15 17,10"/>
          <line x1="12" y1="15" x2="12" y2="3"/>
        </svg>
      </button>

      {open && (
        <div className="export-dropdown-menu">
          <button className="export-dropdown-item" onClick={handleExportExcel}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" strokeWidth="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14,2 14,8 20,8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
              <polyline points="10,9 9,9 8,9"/>
            </svg>
            Excel İndir
          </button>
          <button className="export-dropdown-item" onClick={handleExportPDF}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" strokeWidth="2">
              <path d="M14 2H6a 2 2 0 0 0-2 2v16a 2 2 0 0 0 2 2h12a 2 2 0 0 0 2-2V8z"/>
              <polyline points="14,2 14,8 20,8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
              <polyline points="10,9 9,9 8,9"/>
            </svg>
            PDF İndir
          </button>
        </div>
      )}
    </div>
  );
};

export default ExportMenu;