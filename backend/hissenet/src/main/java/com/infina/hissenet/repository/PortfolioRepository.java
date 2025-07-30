package com.infina.hissenet.repository;

import com.infina.hissenet.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    // Müşteriye ait tüm portföyleri getir
    List<Portfolio> findByCustomerId(Long customerId);
    
    // Aktif portföyleri getir
    List<Portfolio> findByIsActiveTrue();
    
    // Risk profiline göre portföyleri getir
    List<Portfolio> findByRiskProfile(String riskProfile);
    
    // Portföy türüne göre portföyleri getir
    List<Portfolio> findByPortfolioType(String portfolioType);
}
