import React from 'react';
import './NewCustomer.css';

const NewCustomer = () => {
  return (
    <div className="new-customer">
      <div className="page-header">
        <h1>Yeni Müşteri</h1>
        <p>Yeni müşteri kaydı oluşturun</p>
      </div>
      
      <div className="page-content">
        <div className="content-card">
          <h2>Müşteri Bilgileri</h2>
          <p>Bu sayfa yeni müşteri kayıt formunu içerecek.</p>
          <div className="placeholder-content">
            <div className="placeholder-item">
              <h3>Bireysel Müşteri</h3>
              <p>Bireysel müşteri kayıt formu burada olacak</p>
            </div>
            <div className="placeholder-item">
              <h3>Kurumsal Müşteri</h3>
              <p>Kurumsal müşteri kayıt formu burada olacak</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NewCustomer; 