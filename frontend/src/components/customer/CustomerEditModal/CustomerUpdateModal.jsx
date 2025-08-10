import React, { useState, useEffect } from 'react';
import './CustomerUpdateModal.css';
import useAuthFlow from '../../../hooks/useAuthFlow';
import AuthModal from '../../AuthModal/AuthModal';
import { CodeVerificationModal } from '../../AuthModal/CodeVerificationModal';
import Modal from '../../common/Modal/Modal';

const CustomerUpdateModal = ({ isOpen, onClose, customer, onUpdate }) => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: ''
  });
  const [originalData, setOriginalData] = useState({});
  const [loading, setLoading] = useState(false);
  const [pendingUpdateData, setPendingUpdateData] = useState(null);

  const isCorporate = customer?.customerType === 'CORPORATE';

  const {
    step,         // 'IDLE' | 'ASK_ID' | 'ASK_CODE'
    start,        // kimlik modalını aç
    cancel,       // akışı iptal et
    confirmIdentity,
    confirmCode,
    modalOpen,
    modalProps,           // { variant, title, message, ... }
    closeModal,
    // ↓ deneme hakkı bilgisi
    maxAttempts,
    attemptsLeft,
  } = useAuthFlow(async () => {
    // Doğrulama BAŞARILI: şimdi gerçek güncellemeyi yap
    if (!pendingUpdateData) return;

    try {
      setLoading(true);
      await onUpdate(pendingUpdateData);
      setPendingUpdateData(null);
      onClose();
    } catch (err) {
      console.error('Customer update error:', err);
      // You can show a toast or a modal here if you like
    } finally {
      setLoading(false);
    }
  });

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
  }, [customer, isOpen, isCorporate]);

   const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
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

    if (!hasChanges()) {
      onClose();
      return;
    }

    // Backend'e gönderilecek veriyi hazırla
    const updateData = isCorporate
      ? {
        id: customer.id,
        customerType: 'CORPORATE',
        authorizedPersonName: formData.firstName,
        phone: formData.phone,
        ...(formData.email !== originalData.email ? { email: formData.email } : {}),
      }
      : {
        id: customer.id,
        customerType: 'INDIVIDUAL',
        firstName: formData.firstName,
        lastName: formData.lastName,
        phone: formData.phone,
        ...(formData.email !== originalData.email ? { email: formData.email } : {}),
      };

    setPendingUpdateData(updateData);

    // TC Number ile otomatik doğrulama başlat - form açmadan
    if (customer.tcNumber) {
      console.log('TC Number ile doğrulama başlatılıyor:', customer.tcNumber);
      // useAuthFlow'daki confirmIdentity'i direkt çağır
      confirmIdentity(customer.tcNumber);
    } else {
      console.error('TC Number bulunamadı, doğrulama yapılamıyor');
      // TC Number yoksa direkt güncelleme yap
      try {
        setLoading(true);
        await onUpdate(updateData);
        onClose();
      } catch (err) {
        console.error('Customer update error:', err);
      } finally {
        setLoading(false);
      }
    }
  };

   const handleCancel = () => {
    onClose();
  };

  if (!isOpen) return null;

  return (
    <>
      <div className="customer-update-modal-overlay">
        <div className="customer-update-modal">
          <div className="customer-update-modal-header">
            <h2>Müşteri Güncelle</h2>
            <button className="customer-update-modal-close" onClick={handleCancel}
             disabled={loading}
              aria-label="Close">
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

       <AuthModal
        isOpen={step === 'ASK_ID' && !modalOpen}
        onClose={cancel}
        onConfirm={confirmIdentity}
      />
      <CodeVerificationModal
        isOpen={step === 'ASK_CODE' && !modalOpen}
        onClose={cancel}
        onConfirm={confirmCode}
        maxAttempts={maxAttempts}
        attemptsLeft={attemptsLeft}
      />
      {modalOpen && (
        <Modal
          variant={modalProps.variant}
          title={modalProps.title}
          message={modalProps.message}
          onClose={closeModal}
        />
        )}
    </>
  );
};

export default CustomerUpdateModal;