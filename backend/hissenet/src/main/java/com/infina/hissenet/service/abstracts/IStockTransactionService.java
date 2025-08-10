package com.infina.hissenet.service.abstracts;


import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.dto.response.StockTransactionResponse;

import java.util.List;

public interface IStockTransactionService {
    void saveAll(List<StockTransaction> stockTransactions);
    void createTransactionFromOrder(Order order);
    List<StockTransaction> findAll();
    
    /**
     * Belirtilen portföydeki tüm satın alınan hisse işlemlerini getirir
     * @param portfolioId Portföy ID'si
     * @return Birleştirilmiş hisse işlemleri listesi
     */
    List<StockTransactionResponse> getAllBuyTransactions(Long portfolioId);
    
    /**
     * Hisse takas işlemlerini işler
     * Beklemedeki işlemleri takas edilmiş duruma günceller
     */
    void processStockSettlements();
    
    /**
     * Hisse işlemini başka bir portföye taşır
     * @param transactionId Taşınacak işlem ID'si
     * @param portfolioId Hedef portföy ID'si
     */
    void updatePortfolioIdForStockTransactions(Long transactionId, Long portfolioId);

    /**
     * Belirtilen müşterinin belirli bir hisse kodundaki mevcut hisse miktarını getirir
     * @param customerId Müşteri ID'si
     * @param stockCode Hisse kodu
     * @return Mevcut hisse miktarı
     */
    Integer getQuantityForStockTransactionWithStream(Long customerId, String stockCode);
    
    /**
     * Belirtilen müşterinin toplam hisse sayısını getirir
     * @param customerId Müşteri ID'si
     * @return Toplam hisse sayısı
     */
    Integer getTotalStock(Long customerId);

    List<StockTransaction> transactionsListByCustomerIdAndStockCode(Long customerId, String stockCode);
}
