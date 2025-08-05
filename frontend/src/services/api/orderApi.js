const BASE_URL = 'api/v1/orders';

export const orderApi = {
  

  getOrdersByCustomerId: async (customerId) => {
    const response = await fetch(`${BASE_URL}/by-customer?customerId=${customerId}`);
    if (!response.ok) {
      throw new Error('Müşteri siparişleri getirilemedi');
    }
    return response.json();
  },


};