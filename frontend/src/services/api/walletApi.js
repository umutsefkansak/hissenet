const BASE_URL = '/api/v1/wallet'; // /v1 ekleyin

export const walletApi = {
  getCustomerWalletBalance: async (customerId) => {
    const response = await fetch(`${BASE_URL}/customer/${customerId}/balance`);
    if (!response.ok) {
      throw new Error('Bakiye bilgisi alınamadı');
    }
    return response.json();
  }
};