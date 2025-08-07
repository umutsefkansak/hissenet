import React from 'react';
import './Pagination.css';

const Pagination = ({
                        currentPage = 0,
                        totalPages = 1,
                        totalElements = 0,
                        pageSize = 10,
                        onPageChange,
                        onPageSizeChange,
                        showPageSizeOptions = true,
                        pageSizeOptions = [5, 10, 20, 50],
                        showTotalElements = true,
                        className = ""
                    }) => {
    // Sayfa numaraları (0-based backend için +1 ekliyoruz görüntülemede)
    const displayCurrentPage = currentPage + 1;

    // Gösterilecek sayfa numaralarını hesapla
    const getVisiblePages = () => {
        const delta = 2; // Mevcut sayfadan önce ve sonra kaç sayfa gösterilecek
        const range = [];
        const rangeWithDots = [];

        // Başlangıç ve bitiş hesapla
        const start = Math.max(1, displayCurrentPage - delta);
        const end = Math.min(totalPages, displayCurrentPage + delta);

        // Sayfa numaralarını oluştur
        for (let i = start; i <= end; i++) {
            range.push(i);
        }

        // İlk sayfa ve üç nokta mantığı
        if (start > 1) {
            rangeWithDots.push(1);
            if (start > 2) {
                rangeWithDots.push('...');
            }
        }

        rangeWithDots.push(...range);

        // Son sayfa ve üç nokta mantığı
        if (end < totalPages) {
            if (end < totalPages - 1) {
                rangeWithDots.push('...');
            }
            rangeWithDots.push(totalPages);
        }

        return rangeWithDots;
    };

    const handlePageClick = (page) => {
        if (page >= 1 && page <= totalPages && page !== displayCurrentPage) {
            onPageChange(page - 1); // Backend için 0-based'e çevir
        }
    };

    const handlePageSizeChange = (e) => {
        const newSize = parseInt(e.target.value);
        onPageSizeChange?.(newSize, 0); // Sayfa boyutu değiştiğinde ilk sayfaya git
    };

    if (totalPages <= 1 && !showTotalElements) {
        return null;
    }

    const startRecord = currentPage * pageSize + 1;
    const endRecord = Math.min((currentPage + 1) * pageSize, totalElements);

    return (
        <div className={`pagination-container ${className}`}>
            {showTotalElements && (
                <div className="pagination-info">
                    <span className="total-info">
                        {totalElements > 0
                            ? `${startRecord}-${endRecord} / ${totalElements} kayıt`
                            : 'Kayıt bulunamadı'
                        }
                    </span>
                </div>
            )}

            {totalPages > 1 && (
                <div className="pagination-controls">
                    {/* İlk sayfa */}
                    <button
                        className="pagination-btn pagination-btn-nav"
                        onClick={() => handlePageClick(1)}
                        disabled={currentPage === 0}
                        title="İlk sayfa"
                    >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M18 17L13 12L18 7M11 17L6 12L11 7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                    </button>

                    {/* Önceki sayfa */}
                    <button
                        className="pagination-btn pagination-btn-nav"
                        onClick={() => handlePageClick(displayCurrentPage - 1)}
                        disabled={currentPage === 0}
                        title="Önceki sayfa"
                    >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M15 18L9 12L15 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                    </button>

                    {/* Sayfa numaraları */}
                    <div className="pagination-numbers">
                        {getVisiblePages().map((page, index) => (
                            <button
                                key={index}
                                className={`pagination-btn pagination-btn-number ${
                                    page === displayCurrentPage ? 'active' : ''
                                } ${page === '...' ? 'dots' : ''}`}
                                onClick={() => page !== '...' && handlePageClick(page)}
                                disabled={page === '...' || page === displayCurrentPage}
                            >
                                {page}
                            </button>
                        ))}
                    </div>

                    {/* Sonraki sayfa */}
                    <button
                        className="pagination-btn pagination-btn-nav"
                        onClick={() => handlePageClick(displayCurrentPage + 1)}
                        disabled={currentPage === totalPages - 1}
                        title="Sonraki sayfa"
                    >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M9 18L15 12L9 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                    </button>

                    {/* Son sayfa */}
                    <button
                        className="pagination-btn pagination-btn-nav"
                        onClick={() => handlePageClick(totalPages)}
                        disabled={currentPage === totalPages - 1}
                        title="Son sayfa"
                    >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M6 17L11 12L6 7M13 17L18 12L13 7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                    </button>
                </div>
            )}

            {showPageSizeOptions && (
                <div className="pagination-size">
                    <label className="size-label">
                        Sayfa başına:
                        <select
                            value={pageSize}
                            onChange={handlePageSizeChange}
                            className="size-select"
                        >
                            {pageSizeOptions.map(size => (
                                <option key={size} value={size}>{size}</option>
                            ))}
                        </select>
                    </label>
                </div>
            )}
        </div>
    );
};

export default Pagination;