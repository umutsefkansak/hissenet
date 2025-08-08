package com.infina.hissenet.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infina.hissenet.repository.OrderRepository;
import com.infina.hissenet.service.abstracts.IOrderLifecycleService;

@Service
public class OrderLifecycleService implements IOrderLifecycleService {

	private final OrderRepository orderRepository;

	public OrderLifecycleService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	@Transactional
	public void cancelOpenOrdersFor(LocalDate tradingDay) {
		LocalDateTime start = tradingDay.atStartOfDay();
		LocalDateTime end = tradingDay.atTime(23, 59, 59, 999_999_999);
		orderRepository.cancelOpenOrdersInRange(start, end);
	}

}
