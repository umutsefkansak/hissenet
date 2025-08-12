const BASE_URL = '/api/v1/wallet'; 

export const walletApi = {
  
  getCustomerWalletBalance: async (customerId) => {
    const response = await fetch(`${BASE_URL}/customer/${customerId}/balance`);
    if (!response.ok) {
      throw new Error('Bakiye bilgisi alınamadı');
    }
    return response.json();
  },
  getWalletBalance: async (customerId) => {
    const response = await fetch(`${BASE_URL}/customer/${customerId}/balance`);
    if (!response.ok) {
      throw new Error('Bakiye bilgisi getirilemedi');
    }
    return response.json();
  },

  getAvailableBalance: async (customerId) => {
    const response = await fetch(`${BASE_URL}/customer/${customerId}/available-balance`);
    if (!response.ok) {
      throw new Error('Kullanılabilir bakiye bilgisi getirilemedi');
    }
    return response.json();
  },

  getBlockedBalance: async (customerId) => {
    const response = await fetch(`${BASE_URL}/customer/${customerId}/blocked-balance`);
    if (!response.ok) {
      throw new Error('Bloke bakiye bilgisi getirilemedi');
    }
    return response.json();
  },
  
  deposit: async (customerId, amount) => {
    const response = await fetch(`${BASE_URL}/customer/${customerId}/deposit`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: `amount=${amount}`
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.detail || 'Para yükleme işlemi başarısız');
    }
    return response.json();
  },

  withdraw: async (customerId, amount) => {
    const response = await fetch(`${BASE_URL}/customer/${customerId}/withdrawal`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: `amount=${amount}`
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.detail || 'Para çekme işlemi başarısız');
    }
    return response.json();
  }
};