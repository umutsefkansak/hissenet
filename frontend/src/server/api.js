import axios from 'axios';

// API base URL
const API_BASE_URL = '/api/v1';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Login function
export const login = async (email, password) => {
  try {
    const response = await api.post('/auth/login', {
      email,
      password
    });
    
    // If login successful, set isLogin to true and store in localStorage
    if (response.data) {
      localStorage.setItem('isLogin', 'true');
      localStorage.setItem('user', JSON.stringify(response.data));
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
    const response = await api.get('/employee');
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Get employees error:', error);
    return { success: false, error: error.response?.data || 'Failed to get employees' };
  }
};

// Logout function
export const logout = async () => {
  try {
    const response = await api.delete('/auth/logout');
    // Clear localStorage
    localStorage.removeItem('isLogin');
    localStorage.removeItem('user');
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Logout error:', error);
    // Even if API call fails, clear localStorage
    localStorage.removeItem('isLogin');
    localStorage.removeItem('user');
    return { success: false, error: error.response?.data || 'Logout failed' };
  }
};

export default api; 