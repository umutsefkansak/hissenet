import axios from 'axios';

let isRedirecting = false;

const API_BASE_URL = '/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.response.use(
  response => response,
  error => {
    if (
      error.response &&
      error.response.status === 401 &&
      !isRedirecting
    ) {
      isRedirecting = true;
      localStorage.removeItem('isLogin');
      window.dispatchEvent(new Event('loginStateChanged'));
      // window.location.href = '/'; // Yönlendirme kaldırıldı
    }
    return Promise.reject(error);
  }
);

export default api; 