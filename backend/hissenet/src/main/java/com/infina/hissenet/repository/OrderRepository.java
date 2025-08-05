package com.infina.hissenet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
	List<Order> findByStatus(OrderStatus status);
	List<Order> findByCustomerIdAndStockCodeAndStatus(Long customerId, String stockCode, OrderStatus status);
	List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);
	List<Order> findByCustomerId(Long customerId);

}
