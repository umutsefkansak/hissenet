import { useState, useCallback } from 'react';
import { verifyCode, sendVerificationByIdentification } from '../server/mail';
import { getCustomerByEmail } from '../server/customer';

export default function useAuthFlow(onSuccessRedirect) {
    const [step, setStep] = useState('IDLE');
    const [email, setMail] = useState('');
    const [isProcessing, setIsProcessing] = useState(false); // İşlem durumu kontrolü

    const [modalOpen, setModalOpen] = useState(false);
    const [modalProps, setModalProps] = useState({
        variant: 'error',
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
        if (isProcessing) return;
        setModalProps({ variant, title, message });
        setModalOpen(true);
    }, [isProcessing]);

    const closeModal = useCallback(() => setModalOpen(false), []);

    const start = useCallback(() => setStep('ASK_ID'), []);

    const cancel = useCallback(() => {
        setStep('IDLE');
        setIsProcessing(false);
        setModalOpen(false);
    }, []);

    const confirmIdentity = useCallback(async (tc) => {
        if (isProcessing) return;
        setIsProcessing(true);

        try {
            const result = await sendVerificationByIdentification(tc);
            if (!result?.success) {
                openModal('error', 'İşlem Başarısız', 'Doğrulama kodu gönderilemedi.');
                return;
            }
            const inner = result.data.data;
            const email = inner?.email;

            if (!email) {
                openModal('warning', 'Kullanıcı Bulunamadı', 'Bu T.C. Kimlik No/VKN ile kayıtlı müşteri bulunamadı.');
                return;
            }

            setMaxAttempts(inner?.maxAttempts ?? null);
            setRemainingAttempts(inner?.remainingAttempts ?? inner?.maxAttempts ?? null);
            setLocalFailedCount(0);

            setMail(email);
            setStep('ASK_CODE');

            if (inner?.maxAttempts) {
                openModal('success', 'Kod Gönderildi', `Maksimum deneme hakkınız: ${inner.maxAttempts}/${inner.maxAttempts}`);
            }
        } catch (e) {
            openModal('error', 'E-posta Gönderilemedi', 'Sunucuya ulaşılamadı ya da işlem sırasında bir hata oluştu. Lütfen daha sonra tekrar deneyin.');
        } finally {
            setIsProcessing(false);
        }
    }, [openModal, isProcessing]);

    const confirmCode = useCallback(async (code) => {
        if (isProcessing) return;
        setIsProcessing(true);

        try {
            const result = await verifyCode(email, code);
            const apiData = result?.data?.data;
            const isOk = apiData?.success === true;

            if (isOk) {
                try {
                    const customerResp = await getCustomerByEmail(email);
                    const customerId = customerResp?.data?.id;
                    const firstName = customerResp?.data?.firstName;
                    const lastName = customerResp?.data?.lastName;
                    const companyName = customerResp?.data?.companyName;

                    if (!customerId) {
                        openModal('warning', 'Müşteri Bulunamadı', 'Bu e-posta ile kayıtlı müşteri bulunamadı.');
                        return;
                    }

                    localStorage.setItem('customerId', String(customerId));
                    localStorage.setItem('customerEmail', email);
                    localStorage.setItem('customerFirstName', firstName || companyName || '');
                    localStorage.setItem('customerLastName', lastName || '');

                    setStep('IDLE');
                    setModalOpen(false);

                    setTimeout(() => {
                        onSuccessRedirect?.(customerId);
                    }, 100);

                    return;

                } catch (customerError) {
                    console.error('Customer fetch error:', customerError);
                    openModal('error', 'Müşteri Sorgu Hatası', 'Müşteri bilgileri alınırken bir sorun oluştu.');
                    return;
                }
            }

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

        } catch (error) {
            console.error('Code verification error:', error);
            openModal('error', 'Doğrulama Hatası', 'Kod doğrulama sırasında bir hata oluştu.');
        } finally {
            setIsProcessing(false);
        }
    }, [email, onSuccessRedirect, maxAttempts, localFailedCount, remainingAttempts, isProcessing, openModal]);

    return {
        step, start, cancel,
        confirmIdentity, confirmCode,
        modalOpen, modalProps, closeModal,
        maxAttempts, attemptsLeft,
        isProcessing
    };
}
