import api from './api';

export const orderApi = {
  getOrdersByCustomerId: async (customerId) => {
    const res = await api.get('/orders/by-customer', { params: { customerId } });
    return res.data;
  },

  createOrder: async (orderData) => {
    try {
      const res = await api.post('/orders', orderData);
      return res.data;
    } catch (err) {
      const msg = err?.response?.data?.message || 'Emir oluşturulamadı';
      throw new Error(msg);
    }
  },

  getTodayTotalTradeVolume: async () => {
    const res = await api.get('/orders/filled/today/volume');
    return res.data;
  },

  getLastFiveOrders: async () => {
    const res = await api.get('/orders/recent');
    return res.data;
  },

  getTodayFilledOrders: async () => {
    const res = await api.get('/orders/filled/today');
    return res.data;
  },

  getPopularStockCodes: async () => {
    const res = await api.get('/orders/popular');
    return res.data;
  },

  getTotalTradeVolume: async () => {
    const res = await api.get('/orders/volume/total');
    return res.data;
  },

  getAllOrders: async () => {
    const res = await api.get('/orders');
    return res.data;
  },

  getTodayOrderCount: async () => {
    const res = await api.get('/orders/today/count');
    return res.data;
  },

  getAvailableQuantity: async (customerId, stockCode) => {
    const res = await api.get('/orders/available-quantity', { params: { customerId, stockCode } });
    return res.data?.data;
  },

  updateOrder: async (orderId, updateData) => {
    try {
      const res = await api.patch(`/orders/${orderId}`, updateData);
      return res.data;
    } catch (err) {
      const msg = err?.response?.data?.message || 'Emir güncellenemedi';
      throw new Error(msg);
    }
  },

  getOrdersByCustomerIdSorted: async (customerId) => {
    const res = await api.get('/orders/by-customer/sorted', { params: { customerId } });
    return res.data;
  },
};
