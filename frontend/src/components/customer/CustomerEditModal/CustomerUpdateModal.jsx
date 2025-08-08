import React, { useState, useEffect } from 'react';
import './CustomerUpdateModal.css';

const CustomerUpdateModal = ({ isOpen, onClose, customer, onUpdate }) => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: ''
  });
  const [originalData, setOriginalData] = useState({});
  const [loading, setLoading] = useState(false);
  const isCorporate = customer?.customerType === 'CORPORATE';

  useEffect(() => {
    if (customer && isOpen) {
     const initialData = isCorporate
  ? {
      // Kurumsal: yetkili kişi adı tek alandan gelir
      firstName: customer.authorizedPersonName || '',
      lastName: '',
      email: customer.email || '',
      phone: customer.phone || ''
    }
  : {
      firstName: customer.firstName || '',
      lastName: customer.lastName || '',
      email: customer.email || '',
      phone: customer.phone || ''
    };
      
      setFormData(initialData);
      setOriginalData(initialData);
    }
  }, [customer, isOpen]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // Verilerin değişip değişmediğini kontrol et
  const hasChanges = () => {
    return (
      formData.firstName !== originalData.firstName ||
      (!isCorporate && formData.lastName !== originalData.lastName) ||
      formData.email !== originalData.email ||
      formData.phone !== originalData.phone
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Eğer hiçbir değişiklik yoksa, istek gönderme
    if (!hasChanges()) {
      console.log('No changes detected, closing modal without update');
      onClose();
      return;
    }

    setLoading(true);

    try {
      // Backend'e gönderilecek veriyi hazırla
      const updateData = isCorporate
  ? {
      id: customer.id,
      customerType: 'CORPORATE',
      authorizedPersonName: formData.firstName,
      phone: formData.phone
    }
  : {
      id: customer.id,
      customerType: 'INDIVIDUAL',
      firstName: formData.firstName,
      lastName: formData.lastName,
      phone: formData.phone
    };

      // Email sadece değişmişse gönder
      if (formData.email !== originalData.email) {
        updateData.email = formData.email;
      }

      console.log('Sending update data:', updateData);

      await onUpdate(updateData);
      onClose();
    } catch (error) {
      console.error('Müşteri güncellenirken hata:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="customer-update-modal-overlay">
      <div className="customer-update-modal">
        <div className="customer-update-modal-header">
          <h2>Müşteri Güncelle</h2>
          <button className="customer-update-modal-close" onClick={handleCancel}>
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit} className="customer-update-form">
        <div className="form-group">
          <label htmlFor="name">{isCorporate ? 'Yetkili Kişi' : 'Ad Soyad'}</label>
          {isCorporate ? (
            <input
              type="text"
              name="firstName"
              value={formData.firstName}
              onChange={handleInputChange}
              placeholder="Yetkili kişi adı"
              required
            />
          ) : (
            <div className="name-inputs">
              <input
                type="text"
                name="firstName"
                value={formData.firstName}
                onChange={handleInputChange}
                placeholder="Ad"
                required
              />
              <input
                type="text"
                name="lastName"
                value={formData.lastName}
                onChange={handleInputChange}
                placeholder="Soyad"
                required
              />
            </div>
          )}
        </div>

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              placeholder="Email"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="phone">Telefon</label>
            <input
              type="tel"
              name="phone"
              value={formData.phone}
              onChange={handleInputChange}
              placeholder="Telefon"
              required
            />
          </div>

          <div className="customer-update-modal-actions">
            <button
              type="button"
              className="btn-cancel"
              onClick={handleCancel}
              disabled={loading}
            >
              İptal
            </button>
            <button
              type="submit"
              className="btn-update"
              disabled={loading || !hasChanges()}
            >
              {loading ? 'Güncelleniyor...' : 'Güncelle'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CustomerUpdateModal;