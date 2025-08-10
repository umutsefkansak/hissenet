package com.infina.hissenet.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.infina.hissenet.dto.response.*;

import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.exception.transaction.InsufficientStockException;
import com.infina.hissenet.repository.WalletRepository;
import com.infina.hissenet.service.abstracts.ICacheManagerService;
import com.infina.hissenet.service.abstracts.IStockTransactionService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
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
import com.infina.hissenet.service.abstracts.IWalletService;
import com.infina.hissenet.utils.GenericServiceImpl;
import com.infina.hissenet.utils.MessageUtils;

import static com.infina.hissenet.constants.OrderConstants.COMMISSION_RATE;
/**
* t+2 ile ilgili işlemler biraz kafa karıştırıcı olacağı için
* methodlara yorum satırı ekledim :DD
* */
@Service
public class OrderService extends GenericServiceImpl<Order, Long> implements IOrderService {

	private final OrderRepository orderRepository;
	private final CustomerService customerService;
	private final OrderMapper orderMapper;
	private final IWalletService walletService;
	private final ICacheManagerService stockCacheService;
	private final WalletRepository walletRepository;
	private final IStockTransactionService stockTransactionService;
	private final MarketHourService marketHourService;

	public OrderService(OrderRepository orderRepository, CustomerService customerService,
						OrderMapper orderMapper, IWalletService walletService, ICacheManagerService stockCacheService,
						WalletRepository walletRepository, IStockTransactionService stockTransactionService, MarketHourService marketHourService) {
		super(orderRepository);
		this.orderRepository = orderRepository;
		this.customerService = customerService;
		this.orderMapper = orderMapper;
		this.walletService = walletService;
		this.stockCacheService = stockCacheService;
		this.walletRepository = walletRepository;
		this.stockTransactionService = stockTransactionService;
		this.marketHourService = marketHourService;
	}

	@Transactional
	public OrderResponse createOrder(OrderCreateRequest request) {
		/*if (!marketHourService.canPlaceOrder()){
			throw new IllegalStateException();
		}*/
		Customer customer = customerService.findById(request.customerId())
				.orElseThrow(() -> new CustomerNotFoundException(request.customerId()));

		if (request.price() == null || request.quantity() == null) {
			throw new IllegalArgumentException(MessageUtils.getMessage("order.price.quantity.required"));
		}

		// t+2 sürede satış sınırı için
		if (request.type() == OrderType.SELL) {
			validateSellOrder(request.customerId(), request.stockCode(), request.quantity());
		}

		Order order = orderMapper.toEntity(request);
		order.setCustomer(customer);

		BigDecimal totalAmount = request.price().multiply(request.quantity());
		order.setTotalAmount(totalAmount);

		try {
			if (request.category() == OrderCategory.MARKET) {
				processMarketOrder(request, order, totalAmount);
			} else if (request.category() == OrderCategory.LIMIT) {
				processLimitOrder(request, order, totalAmount);
			} else {
				order.setStatus(OrderStatus.REJECTED);
			}
		} catch (RuntimeException e) {
			order.setStatus(OrderStatus.REJECTED);
		}

		Order saved = save(order);

		stockTransactionService.createTransactionFromOrder(saved);

		return orderMapper.toResponse(saved);
	}
	private void processMarketOrder(OrderCreateRequest request, Order order, BigDecimal totalAmount) {
		/*if (!marketHourService.isMarketOpen()){
			order.setStatus(OrderStatus.REJECTED);
			return;
		}*/
		if (request.type() != null) {
			handleWalletTransaction(request, order, totalAmount);
			order.setStatus(OrderStatus.FILLED);
		} else {
			order.setStatus(OrderStatus.REJECTED);
		}
	}

	private void processLimitOrder(OrderCreateRequest request, Order order, BigDecimal totalAmount) {
		if (request.price() == null || request.type() == null || request.quantity() == null) {
			order.setStatus(OrderStatus.REJECTED);
			return;
		}

		BigDecimal marketPrice = stockCacheService.getCachedByCode(request.stockCode()).lastPrice();
		boolean isValid = switch (request.type()) {
			case BUY -> marketPrice.compareTo(request.price()) <= 0;
			case SELL -> marketPrice.compareTo(request.price()) >= 0;
		};

		if (isValid) {
		/*	if (!marketHourService.isMarketOpen()){
				order.setStatus(OrderStatus.OPEN);
				return;
			}*/
			handleWalletTransaction(request, order, totalAmount);
			order.setStatus(OrderStatus.FILLED);
		} else {
			order.setStatus(OrderStatus.OPEN);
		}
	}
	private void handleWalletTransaction(OrderCreateRequest request, Order order, BigDecimal totalAmount) {
		Customer customer = order.getCustomer();
		BigDecimal customerRate = customer != null && customer.getCommissionRate() != null
				? customer.getCommissionRate()
				: COMMISSION_RATE;

		BigDecimal commission = totalAmount.multiply(customerRate);

		if (request.type() == OrderType.BUY) {
			walletService.processStockPurchase(request.customerId(), totalAmount, commission);
		} else if (request.type() == OrderType.SELL) {
			walletService.processStockSale(request.customerId(), totalAmount, commission);
		}
	}

	/**
	 * Satış emri öncesi T+2 settlement güvenlik kontrolü yapcaz
	 * Bloke edilen hisseleri hesaplayarak aşırı satışı engellemek amacımız
	 */
	private void validateSellOrder(Long customerId, String stockCode, BigDecimal requestedQuantity) {
		BigDecimal availableQuantity = getAvailableStockQuantityForSale(customerId, stockCode);

		if (requestedQuantity.compareTo(availableQuantity) > 0) {
			BigDecimal blockedQuantity = getBlockedStockQuantity(customerId, stockCode);
			BigDecimal totalOwned = getOwnedStockQuantity(customerId, stockCode);

			throw new InsufficientStockException(
				String.format(
					"Yetersiz hisse! İstenen: %s, Toplam: %s, Blokede (T+2): %s, Satılabilir: %s. " +
					"T+2 settlement nedeniyle satılan hisseler geçici olarak bloke edilmiştir.",
					requestedQuantity,
					totalOwned,
					blockedQuantity,
					availableQuantity
				)
			);
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
		List<Order> orders = orderRepository.findAllByCreatedAtDesc();

		return orders.stream().map(order -> {
			Long customerId = order.getCustomer().getId();
			LocalDateTime createdAt = order.getCreatedAt();

			BigDecimal blockedBalance = BigDecimal.ZERO;

			if (order.getStatus() == OrderStatus.FILLED) {
				LocalDateTime tPlus2 = calculateTPlus2BusinessDays(createdAt);

				if (LocalDateTime.now().isBefore(tPlus2)) {
					blockedBalance = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
				}
			}

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

	private LocalDateTime calculateTPlus2BusinessDays(LocalDateTime startDateTime) {
		int businessDaysAdded = 0;
		LocalDateTime result = startDateTime;

		while (businessDaysAdded < 2) {
			result = result.plusDays(1);
			DayOfWeek day = result.getDayOfWeek();
			if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
				businessDaysAdded++;
			}
		}

		return result;
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

	/**
	 * T+2 settlement kurallarına uygun olarak satılabilir hisse miktarını hesaplar
	 * Bloke edilen (T+2 bekleyen) hisseleri çıkarır
	 */
	@Transactional(readOnly = true)
	public BigDecimal getAvailableStockQuantityForSale(Long customerId, String stockCode) {
		BigDecimal totalOwned = BigDecimal.valueOf(stockTransactionService.getQuantityForStockTransactionWithStream(customerId,stockCode));
		BigDecimal blockedQuantity = getBlockedStockQuantity(customerId, stockCode);

		BigDecimal availableForSale = totalOwned.subtract(blockedQuantity);
		return availableForSale.max(BigDecimal.ZERO); // Negatif olamaz
	}

	/**
	 * T+2 settlement nedeniyle bloke edilen hisse miktarını hesaplar
	 * Test modunda: 1 dakika, Production'da: 2 iş günü
	 */
	@Transactional(readOnly = true)
	public BigDecimal getBlockedStockQuantity(Long customerId, String stockCode) {
		// STOCK TRANSACTİONDAN DENE
		List<StockTransaction> sellOrders = stockTransactionService.transactionsListByCustomerIdAndStockCode(customerId,stockCode);


		System.out.println(sellOrders);

		BigDecimal blockedQuantity = BigDecimal.ZERO;
		LocalDateTime now = LocalDateTime.now();

		for (StockTransaction transaction : sellOrders) {
			LocalDateTime settlementTime = transaction.getSettlementDate();
			if (now.isBefore(settlementTime)) {
				blockedQuantity = blockedQuantity.add(BigDecimal.valueOf(transaction.getQuantity()));
			}
		}
		
		return blockedQuantity;
	}

	/**
	 * T+2 settlement zamanını hesaplar
	 * Test modu: 1 dakika (hızlı test için)
	 * Production: 2 iş günü
	 */
	private LocalDateTime calculateT2SettlementTime(LocalDateTime orderTime) {

		// Test modunda 1.5 dakika (1 dakika 30 saniye) scheuladn dolayo
		return orderTime.plusMinutes(1).plusSeconds(30);
		
		// Production'da 2 iş günü kullanmak için:
		// return calculateTPlus2BusinessDays(orderTime);
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

	/**
	 * Enhanced portfolio with T+2 settlement information
	 */
	@Transactional(readOnly = true)
	public List<EnhancedPortfolioStockResponse> getEnhancedPortfolioByCustomerId(Long customerId) {
		List<PortfolioStockQuantityResponse> basicPortfolio = getPortfolioByCustomerId(customerId);
		
		return basicPortfolio.stream().map(stock -> {
			String stockCode = stock.stockCode();
			BigDecimal totalQuantity = stock.netQuantity();
			BigDecimal blockedQuantity = getBlockedStockQuantity(customerId, stockCode);
			BigDecimal availableQuantity = totalQuantity.subtract(blockedQuantity);
			LocalDateTime earliestUnblock = getEarliestUnblockTime(customerId, stockCode);
			
			return com.infina.hissenet.dto.response.EnhancedPortfolioStockResponse.of(
				stockCode,
				totalQuantity,
				availableQuantity,
				blockedQuantity,
				stock.averagePrice(),
				earliestUnblock
			);
		}).collect(Collectors.toList());
	}

	/**
	 * En erken unlock zamanını
	 */
	private LocalDateTime getEarliestUnblockTime(Long customerId, String stockCode) {
		List<Order> sellOrders = orderRepository
			.findByCustomerIdAndStockCodeAndStatus(customerId, stockCode, OrderStatus.FILLED)
			.stream()
			.filter(order -> order.getType() == OrderType.SELL)
			.toList();
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime earliest = null;
		
		for (Order order : sellOrders) {
			LocalDateTime settlementTime = calculateT2SettlementTime(order.getCreatedAt());
			

			if (now.isBefore(settlementTime)) {
				if (earliest == null || settlementTime.isBefore(earliest)) {
					earliest = settlementTime;
				}
			}
		}
		
		return earliest;
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
	            .map(stockCode -> new PopularStockCodesResponse((String) stockCode))
	            .toList();
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal getTotalTradeVolume() {
		return orderRepository.getTotalTradeVolume();
	}

	@Transactional(readOnly = true)
	public Long getTodayOrderCount() {
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999_999_999);
		return orderRepository.countTodayOrders(startOfDay, endOfDay);
	}

}
