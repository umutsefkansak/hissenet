const BASE_URL = '/api/v1/customers';

export const customerApi = {
  getAllCustomers: async () => {
    const response = await fetch(`${BASE_URL}`);
    if (!response.ok) {
      throw new Error('Müşteriler getirilemedi');
    }
    return response.json();
  },

  getCustomerById: async (id) => {
    const response = await fetch(`${BASE_URL}/${id}`);
    if (!response.ok) {
      throw new Error('Müşteri bulunamadı');
    }
    return response.json();
  }

  
};