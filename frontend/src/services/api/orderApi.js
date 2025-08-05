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

};

