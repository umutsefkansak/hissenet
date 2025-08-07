// src/pages/ModalDemo/ModalDemo.jsx
import React, { useState } from 'react';
import Modal from '../../components/common/Modal/Modal';

export default function ModalDemo() {
  const [modalConfig, setModalConfig] = useState(null);

  // config.objeleri doğrudan modalConfig'e aktarır
  const showModal = config => () => setModalConfig(config);
  const closeModal = () => setModalConfig(null);

  return (
    <div style={{ padding: 20 }}>
      <h1>Modal Component Demo</h1>

      <button
        onClick={showModal({
          variant: 'success',
          title: 'Kayıt Başarılı',
          message: 'Yeni müşteri başarıyla eklendi.',
          confirmText: 'Tamam',
          onClose: closeModal,
        })}
      >
        Show Success
      </button>

      <button
        onClick={showModal({
          variant: 'error',
          title: 'Hata',
          message: 'Bu bilgilerle kullanıcı kayıtlı.',
          confirmText: 'Tamam',
          onClose: closeModal,
        })}
        style={{ marginLeft: 10 }}
      >
        Show Error
      </button>

      <button
        onClick={showModal({
          variant: 'warning',
          title: 'Uyarı',
          message: 'Bu işlem geri alınamaz.\nDevam etmek istiyor musunuz?',
          cancelText: 'Vazgeç',
          confirmText: 'Devam Et',
          onConfirm: () => {
            // uyarı onayı
            closeModal();
            alert('Warning confirmed!');
          },
          onClose: closeModal,
        })}
        style={{ marginLeft: 10 }}
      >
        Show Warning
      </button>

      <button
        onClick={showModal({
          variant: 'confirm',
          title: 'İşlem Onayı Gerekli',
          message:
            '100 adet ABC hissesi almak üzeresiniz.\n' +
            'Fiyat: €40,00 (Piyasa Emri)\n' +
            'Bu işlemi onaylıyor musunuz?',
          cancelText: 'İptal Et',
          confirmText: 'Onayla ve Gönder',
          onConfirm: () => {
            // onay işlemi
            closeModal();
            alert('Confirmed!');
          },
          onClose: closeModal,
        })}
        style={{ marginLeft: 10 }}
      >
        Show Confirm
      </button>

      {modalConfig && <Modal {...modalConfig} />}
    </div>
  );
}
