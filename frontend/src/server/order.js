const BASE_URL = '/api/v1/orders';

export const orderApi = {
  getOrdersByCustomerId: async (customerId) => {
    const response = await fetch(`${BASE_URL}/by-customer?customerId=${customerId}`);
    if (!response.ok) {
      throw new Error('Müşteri siparişleri getirilemedi');
    }
    return response.json();
  },

  createOrder: async (orderData) => {
    const response = await fetch(`${BASE_URL}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(orderData),
    });

    if (!response.ok) {
      const errData = await response.json();
      throw new Error(errData.message || 'Emir oluşturulamadı');
    }

    return response.json();
  },

  getTodayTotalTradeVolume: async () => {
    const response = await fetch(`${BASE_URL}/filled/today/volume`);
    if (!response.ok) {
      throw new Error('Bugünkü toplam işlem hacmi alınamadı');
    }
    return response.json();
  },

  getLastFiveOrders: async () => {
    const response = await fetch(`${BASE_URL}/recent`);
    if (!response.ok) {
      throw new Error('Son 5 emir getirilemedi');
    }
    return response.json();
  },

  getTodayFilledOrders: async () => {
    const response = await fetch(`${BASE_URL}/filled/today`);
    if (!response.ok) {
      throw new Error("Bugünkü FILLED emirleri alınamadı");
    }
    return response.json();
  },

  getPopularStockCodes: async () => {
    const response = await fetch(`${BASE_URL}/popular`);
    if (!response.ok) {
      throw new Error('En popüler hisseler getirilemedi');
    }
    return response.json();
  },

  getTotalTradeVolume: async () => {
    const response = await fetch(`${BASE_URL}/volume/total`);
    if (!response.ok) {
      throw new Error('Toplam işlem hacmi alınamadı');
    }
    return response.json();
  },

  getAllOrders: async () => {
    const response = await fetch(`${BASE_URL}`);
    if (!response.ok) {
      throw new Error('Tüm emirler getirilemedi');
    }
    return response.json();
  }
};
