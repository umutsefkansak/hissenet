package com.infina.hissenet.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.infina.hissenet.service.abstracts.ICacheManagerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
import com.infina.hissenet.dto.response.OrderResponse;
import com.infina.hissenet.dto.response.PopularStockCodesResponse;
import com.infina.hissenet.dto.response.PortfolioStockQuantityResponse;
import com.infina.hissenet.dto.response.RecentOrderResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.enums.OrderCategory;
import com.infina.hissenet.entity.enums.OrderStatus;
import com.infina.hissenet.entity.enums.OrderType;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import com.infina.hissenet.exception.order.OrderNotFoundException;
import com.infina.hissenet.mapper.OrderMapper;
import com.infina.hissenet.repository.OrderRepository;
import com.infina.hissenet.repository.WalletRepository;
import com.infina.hissenet.service.abstracts.IOrderService;
import com.infina.hissenet.service.abstracts.IWalletService;
import com.infina.hissenet.utils.GenericServiceImpl;

import static com.infina.hissenet.constants.OrderConstants.COMMISSION_RATE;

@Service
public class OrderService extends GenericServiceImpl<Order, Long> implements IOrderService {

	private final OrderRepository orderRepository;
	private final CustomerService customerService;
	private final OrderMapper orderMapper;
	private final IWalletService walletService;
	private final ICacheManagerService stockCacheService;
	private final WalletRepository walletRepository;

	public OrderService(OrderRepository orderRepository, CustomerService customerService,
			OrderMapper orderMapper, IWalletService walletService, ICacheManagerService stockCacheService,
			WalletRepository walletRepository) {
		super(orderRepository);
		this.orderRepository = orderRepository;
		this.customerService = customerService;
		this.orderMapper = orderMapper;
		this.walletService = walletService;
		this.stockCacheService = stockCacheService;
		this.walletRepository = walletRepository;
	}

	@Transactional
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
					BigDecimal marketPrice = stockCacheService.getCachedByCode(request.stockCode()).lastPrice();
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

	@Transactional
	public OrderResponse updateOrder(Long id, OrderUpdateRequest request) {
		Order existing = findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (request.status() == OrderStatus.CANCELED) {
			if (existing.getStatus() == OrderStatus.OPEN) {
				existing.setStatus(OrderStatus.CANCELED);
			}
		}

		Order updated = update(existing);
		return orderMapper.toResponse(updated);
	}

	@Transactional(readOnly = true)
	public OrderResponse getOrderById(Long id) {
		Order order = findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		return orderMapper.toResponse(order);
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getAllOrders() {
	    List<Order> orders = findAll();

	    return orders.stream().map(order -> {
	        Long customerId = order.getCustomer().getId();  

	        BigDecimal blockedBalance = walletRepository.findBlockedBalanceByCustomerId(customerId).orElse(BigDecimal.ZERO);

	        return new OrderResponse(
	            order.getId(),
	            customerId,
	            order.getCategory(),
	            order.getType(),
	            order.getStatus(),
	            order.getStockCode(),
	            order.getQuantity(),
	            order.getPrice(),
	            order.getTotalAmount(),
	            order.getCreatedAt(),
	            order.getUpdatedAt(),
	            order.getCreatedBy() != null ? order.getCreatedBy().getId() : null,
	            order.getUpdatedBy() != null ? order.getUpdatedBy().getId() : null,
	            blockedBalance
	        );
	    }).toList();
	}

	@Transactional(readOnly = true)
	public BigDecimal getOwnedStockQuantity(Long customerId, String stockCode) {
		List<Order> filledOrders = orderRepository
				.findByCustomerIdAndStockCodeAndStatus(customerId, stockCode, OrderStatus.FILLED);

		BigDecimal totalBuy = BigDecimal.ZERO;
		BigDecimal totalSell = BigDecimal.ZERO;

		for (Order order : filledOrders) {
			if (order.getType() == OrderType.BUY) {
				totalBuy = totalBuy.add(order.getQuantity());
			} else if (order.getType() == OrderType.SELL) {
				totalSell = totalSell.add(order.getQuantity());
			}
		}

		return totalBuy.subtract(totalSell);
	}

	@Transactional(readOnly = true)
	public List<PortfolioStockQuantityResponse> getPortfolioByCustomerId(Long customerId) {
		List<Order> filledOrders = orderRepository.findByCustomerIdAndStatus(customerId, OrderStatus.FILLED);

		Map<String, BigDecimal> quantityMap = new HashMap<>();
		Map<String, BigDecimal> totalPriceMap = new HashMap<>();

		for (Order order : filledOrders) {
			String stockCode = order.getStockCode();
			BigDecimal quantity = order.getQuantity();
			BigDecimal price = order.getPrice();
			BigDecimal totalAmount = price.multiply(quantity);

			if (order.getType() == OrderType.BUY) {
				quantityMap.merge(stockCode, quantity, BigDecimal::add);
				totalPriceMap.merge(stockCode, totalAmount, BigDecimal::add);
			} else if (order.getType() == OrderType.SELL) {
				quantityMap.merge(stockCode, quantity.negate(), BigDecimal::add);
			}
		}

		return quantityMap.entrySet().stream()
				.filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) > 0)
				.map(entry -> {
					String stockCode = entry.getKey();
					BigDecimal netQuantity = entry.getValue();
					BigDecimal totalPrice = totalPriceMap.getOrDefault(stockCode, BigDecimal.ZERO);
					BigDecimal averagePrice = netQuantity.compareTo(BigDecimal.ZERO) > 0
							? totalPrice.divide(netQuantity, 2, BigDecimal.ROUND_HALF_UP)
							: BigDecimal.ZERO;
					return new PortfolioStockQuantityResponse(stockCode, netQuantity, averagePrice);
				})
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
		List<Order> orders = orderRepository.findByCustomerId(customerId);
		return orders.stream().map(orderMapper::toResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<RecentOrderResponse> getLastFiveOrders() {
		List<Order> lastFilledOrders = orderRepository.findLastFilledOrders(PageRequest.of(0, 5));
		return lastFilledOrders.stream().map(orderMapper::toRecentResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getAllFilledOrders() {
		List<Order> filledOrders = orderRepository.findByStatus(OrderStatus.FILLED);
		return filledOrders.stream().map(orderMapper::toResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getTodayFilledOrders() {
	    LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
	    LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999_999_999);

	    List<Order> filledToday = orderRepository.findFilledOrdersToday(startOfDay, endOfDay);

	    return filledToday.stream()
	            .map(orderMapper::toResponse)
	            .collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public BigDecimal getTodayTotalTradeVolume() {
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999_999_999);
		return orderRepository.getTodayTotalVolume(startOfDay, endOfDay);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<PopularStockCodesResponse> getPopularStockCodes() {
	    return orderRepository.findPopularStockCodes(PageRequest.of(0, 10))
	            .stream()
	            .map(PopularStockCodesResponse::new)
	            .toList();
	}
	
	@Override
	@Transactional(readOnly = true)
	public BigDecimal getTotalTradeVolume() {
	    return orderRepository.getTotalTradeVolume();
	}

}
