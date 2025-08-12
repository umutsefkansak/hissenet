package com.infina.hissenet.repository;

import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.entity.enums.StockTransactionType;
import com.infina.hissenet.entity.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    // Belirli bir portföye ait işlemler - Join fetch ile
    @Query("SELECT st FROM StockTransaction st " +
           "LEFT JOIN FETCH st.portfolio p " +
           "LEFT JOIN FETCH st.order o " +
           "WHERE p.id = :portfolioId")
    List<StockTransaction> findByPortfolioIdWithJoins(@Param("portfolioId") Long portfolioId);


    // Belirli bir order'a ait işlemler - Join fetch ile
    @Query("SELECT st FROM StockTransaction st " +
           "LEFT JOIN FETCH st.portfolio p " +
           "LEFT JOIN FETCH st.order o " +
           "WHERE o.id = :orderId")
    List<StockTransaction> findByOrderIdWithJoins(@Param("orderId") Long orderId);

    // Tarih aralığına göre işlemler - Join fetch ile
    @Query("SELECT st FROM StockTransaction st " +
           "LEFT JOIN FETCH st.portfolio p " +
           "LEFT JOIN FETCH st.order o " +
           "WHERE st.transactionDate BETWEEN :startDate AND :endDate")
    List<StockTransaction> findByTransactionDateBetweenWithJoins(@Param("startDate") LocalDateTime startDate, 
                                                               @Param("endDate") LocalDateTime endDate);

    // İşlem türüne göre filtreleme - Join fetch ile
    @Query("SELECT st FROM StockTransaction st " +
           "LEFT JOIN FETCH st.portfolio p " +
           "LEFT JOIN FETCH st.order o " +
           "WHERE st.transactionType = :transactionType")
    List<StockTransaction> findByTransactionTypeWithJoins(@Param("transactionType") String transactionType);

    // Tüm işlemleri join fetch ile getir
    @Query("SELECT st FROM StockTransaction st " +
           "LEFT JOIN FETCH st.portfolio p " +
           "LEFT JOIN FETCH st.order o")
    List<StockTransaction> findAllWithJoins();

    // Belirli bir portföye ait işlemler (orijinal metod - geriye uyumluluk için)
    List<StockTransaction> findByPortfolioId(Long portfolioId);

    // Belirli bir hisseye ait işlemler (orijinal metod - geriye uyumluluk için)
    List<StockTransaction> findByStockCode(String stockCode);

    // Belirli bir order'a ait işlemler (orijinal metod - geriye uyumluluk için)
    Optional<StockTransaction> findByOrderId(Long orderId);

    // Tarih aralığına göre işlemler (orijinal metod - geriye uyumluluk için)
    List<StockTransaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // İşlem türüne göre filtreleme (orijinal metod - geriye uyumluluk için)
    @Query("SELECT s FROM StockTransaction s WHERE s.transactionType = :type")
    List<StockTransaction> findByTransactionType(@Param("type") StockTransactionType type);

    @Query("SELECT st FROM StockTransaction st WHERE st.settlementDate <= :currentTime AND st.transactionStatus = :status AND st.transactionType IN (:buyType, :sellType)")
    List<StockTransaction> findStockTransactionsReadyForSettlement(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("status") TransactionStatus status,
            @Param("buyType") StockTransactionType buyType,
            @Param("sellType") StockTransactionType sellType
    );

    // FIFO için: Müşterinin belirli bir hisse için BUY transaction'larını tarih sırasına göre getir
    @Query("SELECT st FROM StockTransaction st " +
           "JOIN st.portfolio p " +
           "WHERE p.customer.id = :customerId " +
           "AND st.stockCode = :stockCode " +
           "AND st.transactionType = :transactionType " +
           "AND st.transactionStatus = :status " +
           "ORDER BY st.transactionDate ASC")
    List<StockTransaction> findByCustomerIdAndStockCodeAndTypeOrderByTransactionDateAsc(
            @Param("customerId") Long customerId,
            @Param("stockCode") String stockCode,
            @Param("transactionType") StockTransactionType transactionType,
            @Param("status") TransactionStatus status
    );

    // FIFO için: Müşterinin belirli bir hisse için belirli status'lerdeki transaction'ları getir
    @Query("SELECT st FROM StockTransaction st " +
            "JOIN st.portfolio p " +
            "WHERE p.customer.id = :customerId " +
            "AND st.stockCode = :stockCode " +
            "AND st.transactionType = :transactionType " +
            "AND st.transactionStatus IN :statuses " +
            "ORDER BY st.createdAt ASC")
    List<StockTransaction> findByCustomerIdAndStockCodeAndTypeAndStatusIn(
            @Param("customerId") Long customerId,
            @Param("stockCode") String stockCode,
            @Param("transactionType") StockTransactionType transactionType,
            @Param("statuses") TransactionStatus statuses
    );

    List<StockTransaction> findByPortfolio_Customer_IdAndStockCode(Long portfolioCustomerİd, String stockCode);

    @Modifying
    @Query("UPDATE StockTransaction st SET st.portfolio.id = :newPortfolioId " +
            "WHERE st.portfolio.customer.id = :customerId AND st.stockCode = :stockCode")
    int updatePortfolioIdByCustomerIdAndStockCode(@Param("newPortfolioId") Long newPortfolioId,
                                                  @Param("customerId") Long customerId,
                                                  @Param("stockCode") String stockCode);



}




