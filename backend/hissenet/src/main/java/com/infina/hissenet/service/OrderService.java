package com.infina.hissenet.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
import com.infina.hissenet.dto.response.OrderResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.enums.OrderCategory;
import com.infina.hissenet.entity.enums.OrderStatus;
import com.infina.hissenet.entity.enums.OrderType;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import com.infina.hissenet.exception.order.OrderNotFoundException;
import com.infina.hissenet.mapper.OrderMapper;
import com.infina.hissenet.repository.OrderRepository;
import com.infina.hissenet.service.abstracts.IOrderService;
import com.infina.hissenet.service.abstracts.IStockCacheService;
import com.infina.hissenet.service.abstracts.IWalletService;
import com.infina.hissenet.utils.GenericServiceImpl;

import static com.infina.hissenet.constants.OrderConstants.COMMISSION_RATE;

@Service
public class OrderService extends GenericServiceImpl<Order, Long> implements IOrderService {

	private final OrderRepository orderRepository;
	private final CustomerService customerService;
	private final OrderMapper orderMapper;
	private final IWalletService walletService;
	private final IStockCacheService stockCacheService;

	public OrderService(OrderRepository orderRepository, CustomerService customerService,
			OrderMapper orderMapper, IWalletService walletService, IStockCacheService stockCacheService) {
		super(orderRepository);
		this.orderRepository = orderRepository;
		this.customerService = customerService;
		this.orderMapper = orderMapper;
		this.walletService = walletService;
		this.stockCacheService = stockCacheService;
	}

	public OrderResponse createOrder(OrderCreateRequest request) {
		Customer customer = customerService.findById(request.customerId())
				.orElseThrow(() -> new CustomerNotFoundException(request.customerId()));

		Order order = orderMapper.toEntity(request);
		order.setCustomer(customer);

		BigDecimal totalAmount = null;
		if (request.price() != null && request.quantity() != null) {
			totalAmount = request.price().multiply(request.quantity());
			order.setTotalAmount(totalAmount);
		}

		try {
			if (request.category() == OrderCategory.MARKET) {
				if (request.type() != null && request.quantity() != null && request.price() != null) {
					handleWalletTransaction(request, totalAmount);
					order.setStatus(OrderStatus.FILLED);
				} else {
					order.setStatus(OrderStatus.REJECTED);
				}

			} else if (request.category() == OrderCategory.LIMIT) {
				if (request.price() == null || request.type() == null || request.quantity() == null) {
					order.setStatus(OrderStatus.REJECTED);
				} else {
					BigDecimal marketPrice = stockCacheService.getPriceByCodeOrNull(request.stockCode());
					boolean isValid = false;

					if (request.type() == OrderType.BUY && marketPrice.compareTo(request.price()) <= 0) {
						isValid = true;
					} else if (request.type() == OrderType.SELL && marketPrice.compareTo(request.price()) >= 0) {
						isValid = true;
					}

					if (isValid) {
						handleWalletTransaction(request, totalAmount);
						order.setStatus(OrderStatus.FILLED);
					} else {
						order.setStatus(OrderStatus.OPEN);
					}
				}
			} else {
				order.setStatus(OrderStatus.REJECTED);
			}
		} catch (RuntimeException e) {
			order.setStatus(OrderStatus.REJECTED);
		}

		Order saved = save(order);
		return orderMapper.toResponse(saved);
	}

	private void handleWalletTransaction(OrderCreateRequest request, BigDecimal totalAmount) {
		BigDecimal commission = totalAmount.multiply(COMMISSION_RATE);

		if (request.type() == OrderType.BUY) {
			walletService.processStockPurchase(request.customerId(), totalAmount, commission);
		} else if (request.type() == OrderType.SELL) {
			walletService.processStockSale(request.customerId(), totalAmount, commission);
		}
	}

	public OrderResponse updateOrder(Long id, OrderUpdateRequest request) {
		Order existing = findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (request.status() == OrderStatus.CANCELED) {
			if (existing.getStatus() == OrderStatus.OPEN) {
				existing.setStatus(OrderStatus.CANCELED);
			} else {
				// log
			}
		}

		Order updated = update(existing);
		return orderMapper.toResponse(updated);
	}

	public OrderResponse getOrderById(Long id) {
		Order order = findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		return orderMapper.toResponse(order);
	}

	public List<OrderResponse> getAllOrders() {
		return findAll().stream().map(orderMapper::toResponse).toList();
	}

}
