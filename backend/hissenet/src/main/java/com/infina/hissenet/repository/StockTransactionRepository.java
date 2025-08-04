package com.infina.hissenet.repository;

import com.infina.hissenet.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    // Belirli bir portföye ait işlemler - Join fetch ile
    @Query("SELECT st FROM StockTransaction st " +
           "LEFT JOIN FETCH st.portfolio p " +
           "LEFT JOIN FETCH st.stock s " +
           "LEFT JOIN FETCH st.order o " +
           "WHERE p.id = :portfolioId")
    List<StockTransaction> findByPortfolioIdWithJoins(@Param("portfolioId") Long portfolioId);

    // Belirli bir hisseye ait işlemler - Join fetch ile
    @Query("SELECT st FROM StockTransaction st " +
           "LEFT JOIN FETCH st.portfolio p " +
           "LEFT JOIN FETCH st.stock s " +
           "LEFT JOIN FETCH st.order o " +
           "WHERE s.id = :stockId")
    List<StockTransaction> findByStockIdWithJoins(@Param("stockId") Long stockId);

    // Belirli bir order'a ait işlemler - Join fetch ile
    @Query("SELECT st FROM StockTransaction st " +
           "LEFT JOIN FETCH st.portfolio p " +
           "LEFT JOIN FETCH st.stock s " +
           "LEFT JOIN FETCH st.order o " +
           "WHERE o.id = :orderId")
    List<StockTransaction> findByOrderIdWithJoins(@Param("orderId") Long orderId);

    // Tarih aralığına göre işlemler - Join fetch ile
    @Query("SELECT st FROM StockTransaction st " +
           "LEFT JOIN FETCH st.portfolio p " +
           "LEFT JOIN FETCH st.stock s " +
           "LEFT JOIN FETCH st.order o " +
           "WHERE st.transactionDate BETWEEN :startDate AND :endDate")
    List<StockTransaction> findByTransactionDateBetweenWithJoins(@Param("startDate") LocalDateTime startDate, 
                                                               @Param("endDate") LocalDateTime endDate);

    // İşlem türüne göre filtreleme - Join fetch ile
    @Query("SELECT st FROM StockTransaction st " +
           "LEFT JOIN FETCH st.portfolio p " +
           "LEFT JOIN FETCH st.stock s " +
           "LEFT JOIN FETCH st.order o " +
           "WHERE st.transactionType = :transactionType")
    List<StockTransaction> findByTransactionTypeWithJoins(@Param("transactionType") String transactionType);

    // Tüm işlemleri join fetch ile getir
    @Query("SELECT st FROM StockTransaction st " +
           "LEFT JOIN FETCH st.portfolio p " +
           "LEFT JOIN FETCH st.stock s " +
           "LEFT JOIN FETCH st.order o")
    List<StockTransaction> findAllWithJoins();

    // Belirli bir portföye ait işlemler (orijinal metod - geriye uyumluluk için)
    List<StockTransaction> findByPortfolioId(Long portfolioId);

    // Belirli bir hisseye ait işlemler (orijinal metod - geriye uyumluluk için)
    List<StockTransaction> findByStockId(Long stockId);

    // Belirli bir order'a ait işlemler (orijinal metod - geriye uyumluluk için)
    List<StockTransaction> findByOrderId(Long orderId);

    // Tarih aralığına göre işlemler (orijinal metod - geriye uyumluluk için)
    List<StockTransaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // İşlem türüne göre filtreleme (orijinal metod - geriye uyumluluk için)
    List<StockTransaction> findByTransactionType(String transactionType);
}
