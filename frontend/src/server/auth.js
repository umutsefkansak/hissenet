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
      if (response.data.data.response && response.data.data.response.id) {
        localStorage.setItem('personnelId', response.data.data.response.id.toString());
      }
      
      // Kullanıcının rollerini localStorage'a kaydet
      if (response.data.data.response && response.data.data.response.roleNames) {
        localStorage.setItem('userRoles', JSON.stringify(response.data.data.response.roleNames));
      }
      
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
    localStorage.removeItem('personnelId');
    localStorage.removeItem('userRoles');
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Logout error:', error);
    // Even if API call fails, clear localStorage
    localStorage.removeItem('isLogin');
    localStorage.removeItem('personnelId');
    localStorage.removeItem('userRoles');
    return { success: false, error: error.response?.data || 'Logout failed' };
  }
}; 