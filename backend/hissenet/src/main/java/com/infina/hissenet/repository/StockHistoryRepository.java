package com.infina.hissenet.repository;

import com.infina.hissenet.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    List<StockHistory> findByStockIdOrderByDataDateDesc(Long stockId);
    List<StockHistory> findByStockIdAndDataDateBetween(Long stockId, LocalDate start, LocalDate end);
}
