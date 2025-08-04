import axios from 'axios';


const API_BASE_URL = '/api/v1';


const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// login
export const login = async (email, password) => {
  try {
    const response = await api.post('/auth/login', {
      email,
      password
    });
    
    
    if (response.data) {
      localStorage.setItem('isLogin', 'true');
      
      return { success: true, data: response.data };
    }
  } catch (error) {
    console.error('Login error:', error);
    return { success: false, error: error.response?.data || 'Login failed' };
  }
};

// Get all employees (for testing)
export const getAllEmployees = async () => {
  try {
    const response = await api.get('/employees');
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Get employees error:', error);
    return { success: false, error: error.response?.data || 'Failed to get employees' };
  }
};

// Logout 
export const logout = async () => {
  try {
    const response = await api.delete('/auth/logout');
    
    localStorage.removeItem('isLogin');
    
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Logout error:', error);
    // Even if API call fails, clear localStorage
    localStorage.removeItem('isLogin');
    
    return { success: false, error: error.response?.data || 'Logout failed' };
  }
};

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

// Change password
export const changePassword = async (email, password, confirmNewPassword) => {
  try {
    const response = await api.patch('/employees/changePassword', {
      email,
      password,
      confirmNewPassword
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Change password error:', error);
    // Error response'u daha iyi handle et
    const errorData = error.response?.data;
    const errorMessage = errorData?.message || errorData?.detail || 'Şifre değiştirilemedi';
    return { success: false, error: errorMessage };
  }
};

export default api; 