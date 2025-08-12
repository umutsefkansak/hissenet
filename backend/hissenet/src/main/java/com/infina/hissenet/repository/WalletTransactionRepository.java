package com.infina.hissenet.repository;

import com.infina.hissenet.entity.WalletTransaction;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.entity.enums.TransactionType;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {


    List<WalletTransaction> findByWalletIdOrderByTransactionDateDesc(Long walletId);
    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.settlementDate <= :currentTime AND wt.transactionStatus = :status AND wt.transactionType IN (:purchaseType, :saleType)")
    List<WalletTransaction> findTransactionsReadyForSettlement(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("status") TransactionStatus status,
            @Param("purchaseType") TransactionType purchaseType,
            @Param("saleType") TransactionType saleType
    );
}
