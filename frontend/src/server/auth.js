import api from './api';

// Login
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
    // Güvenlik için herhangi bir hatada genel mesaj döndür
    return { success: false, error: 'Email veya şifre yanlış' };
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