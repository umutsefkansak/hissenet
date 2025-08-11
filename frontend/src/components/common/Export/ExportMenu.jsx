import React, { useState, useRef, useEffect } from 'react';
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { saveAs } from 'file-saver';
import './ExportMenu.css';

const ExportMenu = ({
  data = [],
  columns = [],
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
      columns.forEach((col) => { obj[col.label] = formatCell(col, row); });
      return obj;
    });
    const ws = XLSX.utils.json_to_sheet(rows);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Veriler');
    const buf = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
    saveAs(new Blob([buf], { type: 'application/octet-stream' }), `${filename}.xlsx`);
    setOpen(false);
  };

  const toBase64 = async (url) => {
    const r = await fetch(url);
    const a = await r.arrayBuffer();
    const u8 = new Uint8Array(a);
    let s = '';
    for (let i = 0; i < u8.length; i++) s += String.fromCharCode(u8[i]);
    return btoa(s);
  };

  const loadFirstWorkingFont = async (pdf) => {
    const candidates = [
      { path: fontPath, vfs: 'NotoSans-Regular.ttf', name: 'NotoSans' },
      { path: '/fonts/DejaVuSans.ttf', vfs: 'DejaVuSans.ttf', name: 'DejaVu' }
    ];
    for (const c of candidates) {
      try {
        const b64 = await toBase64(c.path);
        pdf.addFileToVFS(c.vfs, b64);
        pdf.addFont(c.vfs, c.name, 'normal');
        return c.name;
      } catch (_) {}
    }
    return 'helvetica';
  };

  const handleExportPDF = async () => {
    if (!data.length || !columns.length) return;
    const pdf = new jsPDF({ unit: 'mm', format: 'a4' });
    const fontName = await loadFirstWorkingFont(pdf);
    pdf.setFont(fontName, 'normal');
    pdf.setFontSize(18);
    pdf.setTextColor(30, 55, 72);
    pdf.text(String(pdfTitle), 20, 25);
    if (pdfSubtitle) {
      pdf.setFontSize(12);
      pdf.setTextColor(74, 85, 104);
      pdf.text(String(pdfSubtitle), 20, 35);
    }
    const head = [columns.map(c => String(c.label))];
    const body = data.map(row => columns.map(c => String(formatCell(c, row))));
    autoTable(pdf, {
      head,
      body,
      startY: pdfSubtitle ? 42 : 32,
      margin: { left: 20, right: 20 },
      styles: { font: fontName, fontSize: 9, cellPadding: 2, overflow: 'linebreak' },
      headStyles: { font: fontName, fillColor: [30, 58, 138], textColor: [255, 255, 255], halign: 'left' },
      bodyStyles: { font: fontName, textColor: [45, 55, 72] }
    });
    pdf.save(`${filename}.pdf`);
    setOpen(false);
  };

  return (
    <div className={`export-dropdown ${className}`} ref={ref}>
      <button className="export-icon-button" onClick={() => setOpen(o => !o)}>
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
          <polyline points="7,10 12,15 17,10"/>
          <line x1="12" y1="15" x2="12" y2="3"/>
        </svg>
      </button>
      {open && (
        <div className="export-dropdown-menu">
          <button className="export-dropdown-item" onClick={handleExportExcel}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14,2 14,8 20,8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
              <polyline points="10,9 9,9 8,9"/>
            </svg>
            Excel İndir
          </button>
          <button className="export-dropdown-item" onClick={handleExportPDF}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
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
