package com.infina.hissenet.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
	List<Order> findByStatus(OrderStatus status);
	List<Order> findByCustomerIdAndStockCodeAndStatus(Long customerId, String stockCode, OrderStatus status);
	List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);
	List<Order> findByCustomerId(Long customerId);
	List<Order> findByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end);
	
	@Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
	List<Order> findAllByCreatedAtDesc();

	@Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
	List<Order> findRecentOrders(Pageable pageable);
	
	@Query("SELECT o FROM Order o WHERE o.status = com.infina.hissenet.entity.enums.OrderStatus.FILLED ORDER BY o.createdAt DESC")
	List<Order> findLastFilledOrders(Pageable pageable);
	
	@Query("""
		    SELECT COALESCE(SUM(o.totalAmount), 0) 
		    FROM Order o 
		    WHERE o.status = com.infina.hissenet.entity.enums.OrderStatus.FILLED 
		    AND o.createdAt BETWEEN :start AND :end
		""")
		BigDecimal getTodayTotalVolume(LocalDateTime start, LocalDateTime end);
	
	@Query("""
		    SELECT o FROM Order o
		    WHERE o.status = com.infina.hissenet.entity.enums.OrderStatus.FILLED
		    AND o.createdAt BETWEEN :start AND :end
		    ORDER BY o.createdAt DESC
		""")
		List<Order> findFilledOrdersToday(LocalDateTime start, LocalDateTime end);
	
	@Query("""
		    SELECT o.stockCode
		    FROM Order o
		    WHERE o.status = com.infina.hissenet.entity.enums.OrderStatus.FILLED
		    GROUP BY o.stockCode
		    ORDER BY SUM(o.totalAmount) DESC
		""")
		List<String> findPopularStockCodes(Pageable pageable);
	
	@Query("""
		    SELECT COALESCE(SUM(o.totalAmount), 0) 
		    FROM Order o 
		    WHERE o.status = com.infina.hissenet.entity.enums.OrderStatus.FILLED
		""")
		BigDecimal getTotalTradeVolume();
	
	@Query("""
		    SELECT COUNT(o) 
		    FROM Order o 
		    WHERE o.createdAt BETWEEN :start AND :end
		""")
		Long countTodayOrders(LocalDateTime start, LocalDateTime end);
	
}
