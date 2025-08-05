import api from './api';

// Send verification code
export const sendVerificationCode = async (email) => {
  try {
    const response = await api.post('/mail/send-verification', {
      email
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Send verification code error:', error);
    // Error response'u daha iyi handle et
    const errorData = error.response?.data;
    const errorMessage = errorData?.message || errorData?.detail || 'Doğrulama kodu gönderilemedi';
    return { success: false, error: errorMessage };
  }
};

// Send password reset code
export const sendPasswordResetCode = async (email) => {
  try {
    const response = await api.post('/mail/send-password-reset', {
      email
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Send password reset code error:', error);
    // Error response'u daha iyi handle et
    const errorData = error.response?.data;
    const errorMessage = errorData?.message || errorData?.detail || 'Şifre sıfırlama kodu gönderilemedi';
    return { success: false, error: errorMessage };
  }
};

// Verify verification code
export const verifyCode = async (email, code) => {
  try {
    const response = await api.post('/mail/verify', {
      email,
      code
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Verify code error:', error);
    // Error response'u daha iyi handle et
    const errorData = error.response?.data;
    const errorMessage = errorData?.message || errorData?.detail || 'Kod doğrulanamadı';
    return { success: false, error: errorMessage };
  }
};

// Send password change token
export const sendPasswordChangeToken = async (email) => {
  try {
    const response = await api.post('/mail/send-password-change-token', {
      email
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Send password change token error:', error);
    // Error response'u daha iyi handle et
    const errorData = error.response?.data;
    const errorMessage = errorData?.message || errorData?.detail || 'Şifre değiştirme linki gönderilemedi';
    return { success: false, error: errorMessage };
  }
};

// Verify password change token
export const verifyPasswordChangeToken = async (token) => {
  try {
    const response = await api.post('/mail/verify-password-change-token', {
      token
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Verify password change token error:', error);
    // Error response'u daha iyi handle et
    const errorData = error.response?.data;
    const errorMessage = errorData?.message || errorData?.detail || 'Şifre değiştirme linki doğrulanamadı';
    return { success: false, error: errorMessage };
  }
}; 