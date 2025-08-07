import api from './api';

export const portfolioApi = {
  // Get customer portfolios
  getCustomerPortfolios: async (customerId) => {
    try {
      const response = await api.get(`/portfolio/customer/${customerId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching customer portfolios:', error);
      throw error;
    }
  },

  // Get portfolio details
  getPortfolio: async (portfolioId) => {
    try {
      const response = await api.get(`/portfolio/${portfolioId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching portfolio:', error);
      throw error;
    }
  },

  // Get stock transactions for a portfolio
  getPortfolioStockTransactions: async (portfolioId) => {
    try {
      const response = await api.get(`/stock-transactions/buylist/${portfolioId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching portfolio stock transactions:', error);
      throw error;
    }
  },

  // Update portfolio values
  updatePortfolioValues: async (portfolioId) => {
    try {
      const response = await api.patch(`/portfolio/${portfolioId}/values`);
      return response.data;
    } catch (error) {
      console.error('Error updating portfolio values:', error);
      throw error;
    }
  },

  // Create a new portfolio for a customer
  createPortfolio: async (customerId, payload) => {
    try {
      const response = await api.post(`/portfolio/${customerId}`, payload);
      return response.data;
    } catch (error) {
      console.error('Error creating portfolio:', error);
      throw error;
    }
  },

  // Move a stock transaction to another portfolio
  moveStockTransactionToPortfolio: async (stockTransactionId, portfolioId) => {
    try {
      const response = await api.patch(`/stock-transactions/${stockTransactionId}/${portfolioId}`);
      return response.data;
    } catch (error) {
      console.error('Error moving stock transaction:', error);
      throw error;
    }
  }
}; 