import api from './api';

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