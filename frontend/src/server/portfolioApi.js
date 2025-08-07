import api from '../server/api';

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
  }
}; 