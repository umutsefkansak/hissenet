package com.infina.hissenet.repository;

import com.infina.hissenet.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    // Müşteriye ait tüm portföyleri getir
    List<Portfolio> findByCustomerId(Long customerId);
    
    // Aktif portföyleri getir
    List<Portfolio> findByIsActiveTrue();
    
    // Risk profiline göre portföyleri getir
    List<Portfolio> findByRiskProfile(String riskProfile);
    
    // Portföy türüne göre portföyleri getir
    List<Portfolio> findByPortfolioType(String portfolioType);
    
    // Portfolio'yu customer ile birlikte getir (fetch join)
    @Query("SELECT p FROM Portfolio p JOIN FETCH p.customer WHERE p.id = :id")
    Optional<Portfolio> findByIdWithCustomer(@Param("id") Long id);
}
