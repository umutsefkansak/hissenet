package com.infina.hissenet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
import com.infina.hissenet.dto.response.OrderResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.Stock;
import com.infina.hissenet.entity.enums.OrderStatus;
import com.infina.hissenet.exception.CustomerNotFoundException;
import com.infina.hissenet.exception.OrderNotFoundException;
import com.infina.hissenet.exception.StockNotFoundException;
import com.infina.hissenet.mapper.OrderMapper;
import com.infina.hissenet.repository.CustomerRepository;
import com.infina.hissenet.repository.OrderRepository;
import com.infina.hissenet.repository.StockRepository;
import com.infina.hissenet.utils.GenericServiceImpl;

@Service
public class OrderService extends GenericServiceImpl<Order, Long> {

	private final OrderRepository orderRepository;
	private final CustomerRepository customerRepository;
	private final StockRepository stockRepository;
	private final OrderMapper orderMapper;

	public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository,
			StockRepository stockRepository, OrderMapper orderMapper) {
		super(orderRepository);
		this.orderRepository = orderRepository;
		this.customerRepository = customerRepository;
		this.stockRepository = stockRepository;
		this.orderMapper = orderMapper;
	}

	public OrderResponse createOrder(OrderCreateRequest request) {
		Customer customer = customerRepository.findById(request.customerId())
				.orElseThrow(() -> new CustomerNotFoundException(request.customerId()));
		Stock stock = stockRepository.findById(request.stockId())
				.orElseThrow(() -> new StockNotFoundException(request.stockId()));

		Order order = orderMapper.toEntity(request);
		order.setCustomer(customer);
		order.setStock(stock);

		if (request.price() != null) {
			order.setTotalAmount(request.price().multiply(request.quantity()));
		}

		order.setStatus(OrderStatus.OPEN);

		Order saved = save(order);
		return orderMapper.toResponse(saved);
	}

	public OrderResponse getOrderById(Long id) {
		Order order = findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		return orderMapper.toResponse(order);
	}

	public List<OrderResponse> getAllOrders() {
		return findAll().stream().map(orderMapper::toResponse).toList();
	}

	public OrderResponse updateOrder(OrderUpdateRequest request) {
		Order existing = findById(request.id()).orElseThrow(() -> new OrderNotFoundException(request.id()));

		if (request.quantity() != null) {
			existing.setQuantity(request.quantity());
		}

		if (request.price() != null) {
			existing.setPrice(request.price());
			existing.setTotalAmount(request.price().multiply(existing.getQuantity()));
		}

		if (request.status() != null && request.status() != OrderStatus.PENDING) {
			existing.setStatus(request.status());
		}

		Order updated = update(existing);
		return orderMapper.toResponse(updated);
	}

	public void deleteOrder(Long id) {
		Order existing = findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		delete(existing);
	}

	public OrderResponse markAsFilled(Long id) {
		Order existing = findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		existing.setStatus(OrderStatus.FILLED);
		Order updated = update(existing);
		return orderMapper.toResponse(updated);
	}

	public OrderResponse cancelOrder(Long id) {
		Order existing = findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		existing.setStatus(OrderStatus.CANCELED);
		Order updated = update(existing);
		return orderMapper.toResponse(updated);
	}

	public OrderResponse rejectOrder(Long id) {
		Order existing = findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		existing.setStatus(OrderStatus.REJECTED);
		Order updated = update(existing);
		return orderMapper.toResponse(updated);
	}
	
}
