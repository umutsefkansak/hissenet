// src/hooks/useAuthFlow.js
import { useState, useCallback } from 'react';
import { verifyCode, sendVerificationByIdentification } from '../server/mail';
import { getCustomerByEmail } from '../server/customer';

/**
 * onSuccessRedirect: Doğrulama tamamlandığında çağrılacak yönlendirme fonksiyonu
 */
export default function useAuthFlow(onSuccessRedirect) {
    const [step, setStep] = useState('IDLE');      // 'IDLE' | 'ASK_ID' | 'ASK_CODE'
    const [email, setMail] = useState('');

    const [modalOpen, setModalOpen] = useState(false);
    const [modalProps, setModalProps] = useState({
        variant: 'error',   // 'error' | 'warning' | 'success' | 'confirm' ...
        title: '',
        message: ''
    });
    const [maxAttempts, setMaxAttempts] = useState(null);
    const [remainingAttempts, setRemainingAttempts] = useState(null);
    const [localFailedCount, setLocalFailedCount] = useState(0);

    const attemptsLeft = remainingAttempts != null
        ? remainingAttempts
        : (maxAttempts != null ? Math.max(maxAttempts - localFailedCount, 0) : null);

    const openModal = useCallback((variant, title, message) => {
        setModalProps({ variant, title, message });
        setModalOpen(true);
    }, []);
    const closeModal = useCallback(() => setModalOpen(false), []);

    const start = useCallback(() => setStep('ASK_ID'), []);
    const cancel = useCallback(() => setStep('IDLE'), []);

    const confirmIdentity = useCallback(async (tc) => {
        try {
            const result = await sendVerificationByIdentification(tc);
            if (!result?.success) {
                // Axios hata sarmalıyla geldiyse
                openModal('error', 'İşlem Başarısız', 'Doğrulama kodu gönderilemedi.');
                return;
            }
            const inner = result.data.data; // response.data.data'ya eşit olacak şekilde mail servisinde düzenlemiştik
            const email = inner?.email;

            if (!email) {
                // Örn: kullanıcı bulunamadı senaryosu
                openModal('warning', 'Kullanıcı Bulunamadı', 'Bu T.C. Kimlik No/VKN ile kayıtlı müşteri bulunamadı.');
                return;
            }

            // ← ilk kaynağımız: e-posta gönderim cevabı
            setMaxAttempts(inner?.maxAttempts ?? null);
            setRemainingAttempts(inner?.remainingAttempts ?? inner?.maxAttempts ?? null);
            setLocalFailedCount(0); // yeni süreç, sıfırla

            setMail(email);
            setStep('ASK_CODE');

            if (inner?.maxAttempts) {
                openModal('success', 'Kod Gönderildi', `Maksimum deneme hakkınız: ${inner.maxAttempts}/${inner.maxAttempts}`);
            }
        } catch (e) {
            // Sunucuya ulaşılamadı / 5xx / beklenmedik hata
            openModal('error', 'E-posta Gönderilemedi', 'Sunucuya ulaşılamadı ya da işlem sırasında bir hata oluştu. Lütfen daha sonra tekrar deneyin.');
        }
    }, [openModal]);



    const confirmCode = useCallback(async (code) => {
        // verifyCode'u değiştirmiyoruz
        const result = await verifyCode(email, code);
        const apiData = result?.data?.data;
        const isOk = apiData?.success === true;

        if (isOk) {
        try {
            const customerResp = await getCustomerByEmail(email);
            const customerId = customerResp?.data?.id;

            if (!customerId) {
                openModal('warning', 'Müşteri Bulunamadı', 'Bu e-posta ile kayıtlı müşteri bulunamadı.');
                return;
            }

            localStorage.setItem('customerId', String(customerId));
            localStorage.setItem('customerEmail', email);

            setStep('IDLE');
            onSuccessRedirect?.(customerId);
        } catch {
            openModal('error', 'Müşteri Sorgu Hatası', 'Müşteri bilgileri alınırken bir sorun oluştu.');
        }
        return; // ❗ burada bitiriyoruz
    }

    // ❌ Başarısızsa hak kontrolü
    const apiMax = apiData?.maxAttempts ?? maxAttempts;
    const apiRemaining = apiData?.remainingAttempts ?? remainingAttempts;

    if (apiMax != null) setMaxAttempts(apiMax);
    if (apiRemaining != null) {
        setRemainingAttempts(apiRemaining);
    } else {
        setLocalFailedCount(prev => prev + 1);
    }

    const left = apiRemaining ?? (apiMax != null ? Math.max(apiMax - (localFailedCount + 1), 0) : null);
    const pretty = (apiMax != null && left != null) ? ` (${left}/${apiMax})` : '';

    if (left === 0) {
        setMail('');
        setRemainingAttempts(null);
        setLocalFailedCount(0);
        setStep('IDLE');

        openModal(
            'error',
            'Deneme Hakkı Doldu',
            'Bu işlem için çok fazla yanlış deneme yapıldı. Lütfen daha sonra tekrar deneyin.'
        );
        return;
    }

    openModal(
        'error',
        'Kod Doğrulanamadı',
        (apiData?.message || 'Lütfen kodu kontrol edin.') + pretty
    );
}, [email, onSuccessRedirect, maxAttempts, localFailedCount]);

    return {
        step, start, cancel,
        confirmIdentity, confirmCode,
        modalOpen, modalProps, closeModal,
        maxAttempts, attemptsLeft
    };
}
