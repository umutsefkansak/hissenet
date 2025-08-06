package com.infina.hissenet.repository;

import com.infina.hissenet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByCustomerId(Long customerId);
    
    @Query("SELECT w.blockedBalance FROM Wallet w WHERE w.customer.id = :customerId")
    Optional<BigDecimal> findBlockedBalanceByCustomerId(@Param("customerId") Long customerId);

}
