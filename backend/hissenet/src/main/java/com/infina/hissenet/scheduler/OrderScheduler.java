package com.infina.hissenet.scheduler;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.enums.OrderStatus;
import com.infina.hissenet.entity.enums.OrderType;
import com.infina.hissenet.repository.OrderRepository;
import com.infina.hissenet.service.WalletService;

@Component
public class OrderScheduler {

	private final OrderRepository orderRepository;
	private final WalletService walletService;

	public OrderScheduler(OrderRepository orderRepository, WalletService walletService) {
		this.orderRepository = orderRepository;
		this.walletService = walletService;
	}

	@Scheduled(cron = "0 0 * * * *")
	public void processPendingLimitOrders() {
		List<Order> openOrders = orderRepository.findByStatus(OrderStatus.OPEN);

		for (Order order : openOrders) {
			try {
				BigDecimal marketPrice = order.getStock().getCurrentPrice().getCurrentPrice();
				BigDecimal limitPrice = order.getPrice();
				BigDecimal totalAmount = limitPrice.multiply(order.getQuantity());

				boolean shouldFill = false;

				if (order.getType() == OrderType.BUY && marketPrice.compareTo(limitPrice) <= 0) {
					shouldFill = true;
				} else if (order.getType() == OrderType.SELL && marketPrice.compareTo(limitPrice) >= 0) {
					shouldFill = true;
				}

				if (shouldFill) {
					if (order.getType() == OrderType.BUY) {
						walletService.processStockPurchase(order.getCustomer().getId(), totalAmount, BigDecimal.ZERO,
								BigDecimal.ZERO);
					} else {
						walletService.processStockSale(order.getCustomer().getId(), totalAmount, BigDecimal.ZERO,
								BigDecimal.ZERO);
					}

					order.setStatus(OrderStatus.FILLED);
					orderRepository.save(order);
				}
			} catch (Exception e) {
				//log
			}
		}
	}
	
}
