// src/hooks/useAuthFlow.js
import { useState, useCallback } from 'react';
import { verifyCode, sendVerificationByIdentification } from '../server/mail';
import { getCustomerByEmail  } from '../server/customer';

/**
 * onSuccessRedirect: Doğrulama tamamlandığında çağrılacak yönlendirme fonksiyonu
 */
export default function useAuthFlow(onSuccessRedirect) {
    const [step, setStep] = useState('IDLE');      // 'IDLE' | 'ASK_ID' | 'ASK_CODE'
    const [email, setMail] = useState('');

    const start = useCallback(() => setStep('ASK_ID'), []);
    const cancel = useCallback(() => setStep('IDLE'), []);

    const confirmIdentity = useCallback(async (tc) => {
        const result = await sendVerificationByIdentification(tc);
        if (!result.success) {
            throw new Error(result.error);
        }
        setMail(result.data.data.email);
        console.log(result.data.data.email);
        setStep('ASK_CODE');
    }, []);

    const confirmCode = useCallback(async (code) => {
        const result = await verifyCode(email, code);
        if (!result.success) {
            throw new Error(result.error);
        }
        const customerResp = await getCustomerByEmail(email);
        const customerId = customerResp?.data?.id;
        console.log(customerId);
        if (customerId) {
            localStorage.setItem('customerId', String(customerId));
        } else {
            // throw new Error('Müşteri bulunamadı');
        }

        setStep('IDLE');
        onSuccessRedirect();
    }, [email, onSuccessRedirect]);

    return {
        step,
        start,
        cancel,
        confirmIdentity,
        confirmCode,
    };
}
