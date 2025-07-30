package com.infina.hissenet.repository;

import com.infina.hissenet.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
