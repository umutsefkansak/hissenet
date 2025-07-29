package com.infina.hissenet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infina.hissenet.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{

}
