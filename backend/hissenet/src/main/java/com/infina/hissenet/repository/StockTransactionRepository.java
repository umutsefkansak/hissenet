package com.infina.hissenet.repository;

import com.infina.hissenet.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    // Belirli bir portföye ait işlemler
    List<StockTransaction> findByPortfolioId(Long portfolioId);

    // Belirli bir hisseye ait işlemler
    List<StockTransaction> findByStockId(Long stockId);

    // Belirli bir order'a ait işlemler
    List<StockTransaction> findByOrderId(Long orderId);

    // Tarih aralığına göre işlemler
    List<StockTransaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // İşlem türüne göre filtreleme (örneğin: BUY, SELL, DIVIDEND)
    List<StockTransaction> findByTransactionType(String transactionType);
}
