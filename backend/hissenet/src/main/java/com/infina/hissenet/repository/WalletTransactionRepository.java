package com.infina.hissenet.repository;

import com.infina.hissenet.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {


    List<WalletTransaction> findByWalletIdOrderByTransactionDateDesc(Long walletId);
}
