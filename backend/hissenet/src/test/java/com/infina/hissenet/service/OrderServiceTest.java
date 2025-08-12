package com.infina.hissenet.service;

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
import com.infina.hissenet.service.abstracts.ICacheManagerService;
import com.infina.hissenet.service.abstracts.IStockTransactionService;
import com.infina.hissenet.service.abstracts.IWalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CustomerService customerService;
    @Mock private OrderMapper orderMapper;
    @Mock private IWalletService walletService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private ICacheManagerService stockCacheService;
    @Mock private WalletRepository walletRepository;
    @Mock private IStockTransactionService stockTransactionService;
    @Mock private MarketHourService marketHourService;

    @InjectMocks
    private OrderService orderService;

    private OrderCreateRequest buildCreateReq(Long customerId, OrderCategory cat, OrderType type, String stock, BigDecimal qty, BigDecimal price) {
        return new OrderCreateRequest(customerId, cat, type, stock, qty, price);
    }

    private OrderUpdateRequest buildUpdateReq(OrderStatus status) {
        return new OrderUpdateRequest(status);
    }

    private Order newOrder(Long id, Long customerId, OrderStatus status, OrderType type, OrderCategory category, String stock, BigDecimal price, BigDecimal qty, BigDecimal total) {
        Customer c = new Customer();
        c.setId(customerId);
        Order o = new Order();
        o.setId(id);
        o.setCustomer(c);
        o.setStatus(status);
        o.setType(type);
        o.setCategory(category);
        o.setStockCode(stock);
        o.setPrice(price);
        o.setQuantity(qty);
        o.setTotalAmount(total);
        o.setCreatedAt(LocalDateTime.now());
        return o;
    }

    @Test
    void whenCreateOrder_withNullPrice_thenThrowIllegalArgument() {
        OrderCreateRequest req = buildCreateReq(1L, OrderCategory.MARKET, OrderType.BUY, "ARCLK", BigDecimal.ONE, null);
        when(customerService.findById(1L)).thenReturn(Optional.of(new Customer()));
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(req));
    }

    @Test
    void whenCreateOrder_withNullQuantity_thenThrowIllegalArgument() {
        OrderCreateRequest req = buildCreateReq(1L, OrderCategory.MARKET, OrderType.BUY, "ARCLK", null, BigDecimal.TEN);
        when(customerService.findById(1L)).thenReturn(Optional.of(new Customer()));
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(req));
    }

    @Test
    void whenCreateOrder_withNonExistingCustomer_thenThrowCustomerNotFound() {
        OrderCreateRequest req = buildCreateReq(99L, OrderCategory.MARKET, OrderType.BUY, "ARCLK", BigDecimal.ONE, BigDecimal.TEN);
        when(customerService.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> orderService.createOrder(req));
    }

    @Test
    void whenCreateOrder_marketBuy_thenFilledAndWalletPurchaseCalledAndTransactionCreated() {
        OrderCreateRequest req = buildCreateReq(1L, OrderCategory.MARKET, OrderType.BUY, "ARCLK", BigDecimal.valueOf(5), BigDecimal.valueOf(20));
        Customer customer = new Customer(); customer.setId(1L);
        Order mapped = new Order(); mapped.setCustomer(customer); mapped.setCategory(OrderCategory.MARKET); mapped.setType(OrderType.BUY);

        when(customerService.findById(1L)).thenReturn(Optional.of(customer));
        when(orderMapper.toEntity(req)).thenReturn(mapped);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> { Order o = i.getArgument(0); o.setId(10L); o.setStatus(OrderStatus.FILLED); return o; });
        when(orderMapper.toResponse(any(Order.class))).thenReturn(
                new OrderResponse(10L, 1L, OrderCategory.MARKET, OrderType.BUY, OrderStatus.FILLED, "ARCLK",
                        BigDecimal.valueOf(5), BigDecimal.valueOf(20), BigDecimal.valueOf(100), LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        OrderResponse resp = orderService.createOrder(req);

        assertEquals(OrderStatus.FILLED, resp.status());
        verify(walletService).processStockPurchase(eq(1L), eq(BigDecimal.valueOf(100)), any());
        verify(stockTransactionService).createTransactionFromOrder(any(Order.class));
    }

    @Test
    void whenCreateOrder_marketSell_thenFilledAndWalletSaleCalledAndTransactionCreated() {
        OrderCreateRequest req = buildCreateReq(2L, OrderCategory.MARKET, OrderType.SELL, "VESBE", BigDecimal.valueOf(4), BigDecimal.valueOf(15));
        Customer customer = new Customer(); customer.setId(2L);
        Order mapped = new Order(); mapped.setCustomer(customer); mapped.setCategory(OrderCategory.MARKET); mapped.setType(OrderType.SELL);

        when(customerService.findById(2L)).thenReturn(Optional.of(customer));
        when(orderMapper.toEntity(req)).thenReturn(mapped);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> { Order o = i.getArgument(0); o.setStatus(OrderStatus.FILLED); return o; });
        when(orderMapper.toResponse(any(Order.class))).thenReturn(
                new OrderResponse(11L, 2L, OrderCategory.MARKET, OrderType.SELL, OrderStatus.FILLED, "VESBE",
                        BigDecimal.valueOf(4), BigDecimal.valueOf(15), BigDecimal.valueOf(60), LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        OrderResponse resp = orderService.createOrder(req);

        assertEquals(OrderStatus.FILLED, resp.status());
        verify(walletService).processStockSale(eq(2L), eq(BigDecimal.valueOf(60)), any());
        verify(stockTransactionService).createTransactionFromOrder(any(Order.class));
    }

    @Test
    void whenCreateOrder_marketWithNullType_thenRejectedAndTransactionCreated() {
        OrderCreateRequest req = new OrderCreateRequest(1L, OrderCategory.MARKET, null, "ARCLK", BigDecimal.ONE, BigDecimal.TEN);
        Customer c = new Customer(); c.setId(1L);
        Order mapped = new Order(); mapped.setCustomer(c); mapped.setCategory(OrderCategory.MARKET);

        when(customerService.findById(1L)).thenReturn(Optional.of(c));
        when(orderMapper.toEntity(req)).thenReturn(mapped);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(
                new OrderResponse(null, 1L, OrderCategory.MARKET, null, OrderStatus.REJECTED, "ARCLK",
                        BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        OrderResponse resp = orderService.createOrder(req);

        assertEquals(OrderStatus.REJECTED, resp.status());
        verify(stockTransactionService).createTransactionFromOrder(any(Order.class));
        verify(walletService, never()).processStockPurchase(anyLong(), any(), any());
        verify(walletService, never()).processStockSale(anyLong(), any(), any());
    }

    @Test
    void whenCreateOrder_walletServiceThrows_thenRejectedAndStillSavedAndTransactionCreated() {
        OrderCreateRequest req = buildCreateReq(1L, OrderCategory.MARKET, OrderType.BUY, "ARCLK", BigDecimal.ONE, BigDecimal.TEN);
        Customer c = new Customer(); c.setId(1L);
        Order mapped = new Order(); mapped.setCustomer(c); mapped.setCategory(OrderCategory.MARKET); mapped.setType(OrderType.BUY);

        when(customerService.findById(1L)).thenReturn(Optional.of(c));
        when(orderMapper.toEntity(req)).thenReturn(mapped);
        doThrow(new RuntimeException("x")).when(walletService).processStockPurchase(anyLong(), any(), any());
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(
                new OrderResponse(null, 1L, OrderCategory.MARKET, OrderType.BUY, OrderStatus.REJECTED, "ARCLK",
                        BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        OrderResponse resp = orderService.createOrder(req);

        assertEquals(OrderStatus.REJECTED, resp.status());
        verify(stockTransactionService).createTransactionFromOrder(any(Order.class));
    }

    @Test
    void whenCreateOrder_limitBuy_withMarketPriceLeqLimit_thenFilledAndWalletPurchaseCalled() {
        OrderCreateRequest req = buildCreateReq(1L, OrderCategory.LIMIT, OrderType.BUY, "ARCLK", BigDecimal.valueOf(2), BigDecimal.valueOf(30));
        Customer c = new Customer(); c.setId(1L);
        Order mapped = new Order(); mapped.setCustomer(c); mapped.setCategory(OrderCategory.LIMIT); mapped.setType(OrderType.BUY);

        when(customerService.findById(1L)).thenReturn(Optional.of(c));
        when(orderMapper.toEntity(req)).thenReturn(mapped);
        when(stockCacheService.getCachedByCode(eq("ARCLK")).lastPrice()).thenReturn(BigDecimal.valueOf(25));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> { Order o = i.getArgument(0); o.setStatus(OrderStatus.FILLED); return o; });
        when(orderMapper.toResponse(any(Order.class))).thenReturn(
                new OrderResponse(100L, 1L, OrderCategory.LIMIT, OrderType.BUY, OrderStatus.FILLED, "ARCLK",
                        BigDecimal.valueOf(2), BigDecimal.valueOf(30), BigDecimal.valueOf(60), LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        OrderResponse resp = orderService.createOrder(req);

        assertEquals(OrderStatus.FILLED, resp.status());
        verify(walletService).processStockPurchase(eq(1L), eq(BigDecimal.valueOf(60)), any());
    }

    @Test
    void whenCreateOrder_limitSell_withMarketPriceGeqLimit_thenFilledAndWalletSaleCalled() {
        OrderCreateRequest req = buildCreateReq(1L, OrderCategory.LIMIT, OrderType.SELL, "ARCLK", BigDecimal.valueOf(3), BigDecimal.valueOf(20));
        Customer c = new Customer(); c.setId(1L);
        Order mapped = new Order(); mapped.setCustomer(c); mapped.setCategory(OrderCategory.LIMIT); mapped.setType(OrderType.SELL);

        when(customerService.findById(1L)).thenReturn(Optional.of(c));
        when(orderMapper.toEntity(req)).thenReturn(mapped);
        when(stockCacheService.getCachedByCode(eq("ARCLK")).lastPrice()).thenReturn(BigDecimal.valueOf(25));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> { Order o = i.getArgument(0); o.setStatus(OrderStatus.FILLED); return o; });
        when(orderMapper.toResponse(any(Order.class))).thenReturn(
                new OrderResponse(101L, 1L, OrderCategory.LIMIT, OrderType.SELL, OrderStatus.FILLED, "ARCLK",
                        BigDecimal.valueOf(3), BigDecimal.valueOf(20), BigDecimal.valueOf(60), LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        OrderResponse resp = orderService.createOrder(req);

        assertEquals(OrderStatus.FILLED, resp.status());
        verify(walletService).processStockSale(eq(1L), eq(BigDecimal.valueOf(60)), any());
    }

    @Test
    void whenCreateOrder_limitBuy_withMarketPriceGtLimit_thenOpenAndNoWalletCall() {
        OrderCreateRequest req = buildCreateReq(1L, OrderCategory.LIMIT, OrderType.BUY, "ARCLK", BigDecimal.ONE, BigDecimal.valueOf(20));
        Customer c = new Customer(); c.setId(1L);
        Order mapped = new Order(); mapped.setCustomer(c); mapped.setCategory(OrderCategory.LIMIT); mapped.setType(OrderType.BUY);

        when(customerService.findById(1L)).thenReturn(Optional.of(c));
        when(orderMapper.toEntity(req)).thenReturn(mapped);
        when(stockCacheService.getCachedByCode(eq("ARCLK")).lastPrice()).thenReturn(BigDecimal.valueOf(25));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(
                new OrderResponse(null, 1L, OrderCategory.LIMIT, OrderType.BUY, OrderStatus.OPEN, "ARCLK",
                        BigDecimal.ONE, BigDecimal.valueOf(20), BigDecimal.valueOf(20), LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        OrderResponse resp = orderService.createOrder(req);

        assertEquals(OrderStatus.OPEN, resp.status());
        verify(walletService, never()).processStockPurchase(anyLong(), any(), any());
    }

    @Test
    void whenCreateOrder_limitSell_withMarketPriceLtLimit_thenOpenAndNoWalletCall() {
        OrderCreateRequest req = buildCreateReq(1L, OrderCategory.LIMIT, OrderType.SELL, "ARCLK", BigDecimal.ONE, BigDecimal.valueOf(30));
        Customer c = new Customer(); c.setId(1L);
        Order mapped = new Order(); mapped.setCustomer(c); mapped.setCategory(OrderCategory.LIMIT); mapped.setType(OrderType.SELL);

        when(customerService.findById(1L)).thenReturn(Optional.of(c));
        when(orderMapper.toEntity(req)).thenReturn(mapped);
        when(stockCacheService.getCachedByCode(eq("ARCLK")).lastPrice()).thenReturn(BigDecimal.valueOf(25));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(
                new OrderResponse(null, 1L, OrderCategory.LIMIT, OrderType.SELL, OrderStatus.OPEN, "ARCLK",
                        BigDecimal.ONE, BigDecimal.valueOf(30), BigDecimal.valueOf(30), LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        OrderResponse resp = orderService.createOrder(req);

        assertEquals(OrderStatus.OPEN, resp.status());
        verify(walletService, never()).processStockSale(anyLong(), any(), any());
    }

    @Test
    void whenCreateOrder_limitMissingFields_thenRejected() {
        OrderCreateRequest req = new OrderCreateRequest(
                1L, OrderCategory.LIMIT, OrderType.BUY, "ARCLK", BigDecimal.ONE, null
        );

        Customer c = new Customer(); c.setId(1L);
        when(customerService.findById(1L)).thenReturn(Optional.of(c));

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(req));

        verify(orderRepository, never()).save(any(Order.class));
        verify(walletService, never()).processStockPurchase(anyLong(), any(), any());
        verify(walletService, never()).processStockSale(anyLong(), any(), any());
    }

    @Test
    void whenUpdateOrder_cancelOpen_thenCanceled() {
        Order existing = newOrder(1L, 10L, OrderStatus.OPEN, OrderType.BUY, OrderCategory.LIMIT, "ARCLK", BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);
        when(orderMapper.toResponse(existing)).thenReturn(
                new OrderResponse(1L, 10L, OrderCategory.LIMIT, OrderType.BUY, OrderStatus.CANCELED, "ARCLK",
                        BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        OrderResponse resp = orderService.updateOrder(1L, buildUpdateReq(OrderStatus.CANCELED));

        assertEquals(OrderStatus.CANCELED, resp.status());
    }

    @Test
    void whenUpdateOrder_cancelNonOpen_thenUnchanged() {
        Order existing = newOrder(2L, 10L, OrderStatus.FILLED, OrderType.BUY, OrderCategory.MARKET, "ARCLK", BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN);

        when(orderRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);
        when(orderMapper.toResponse(existing)).thenReturn(
                new OrderResponse(2L, 10L, OrderCategory.MARKET, OrderType.BUY, OrderStatus.FILLED, "ARCLK",
                        BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        OrderResponse resp = orderService.updateOrder(2L, buildUpdateReq(OrderStatus.CANCELED));

        assertEquals(OrderStatus.FILLED, resp.status());
    }

    @Test
    void whenUpdateOrder_nonExisting_thenThrowOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(999L, buildUpdateReq(OrderStatus.CANCELED)));
    }

    @Test
    void whenGetOrderById_existing_thenReturnResponse() {
        Order o = newOrder(3L, 7L, OrderStatus.FILLED, OrderType.SELL, OrderCategory.MARKET, "VESBE", BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN);

        when(orderRepository.findById(3L)).thenReturn(Optional.of(o));
        when(orderMapper.toResponse(o)).thenReturn(
                new OrderResponse(3L, 7L, OrderCategory.MARKET, OrderType.SELL, OrderStatus.FILLED, "VESBE",
                        BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        OrderResponse resp = orderService.getOrderById(3L);

        assertEquals(3L, resp.id());
    }

    @Test
    void whenGetOrderById_notExisting_thenThrowOrderNotFound() {
        when(orderRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(404L));
    }

    @Test
    void whenGetAllOrders_thenBlockedBalanceAppliedForRecentFilledOnly() {
        Order recentFilled = newOrder(1L, 1L, OrderStatus.FILLED, OrderType.BUY, OrderCategory.MARKET, "ARCLK", BigDecimal.valueOf(10), BigDecimal.valueOf(2), BigDecimal.valueOf(20));
        recentFilled.setCreatedAt(LocalDateTime.now());
        Order oldFilled = newOrder(2L, 1L, OrderStatus.FILLED, OrderType.BUY, OrderCategory.MARKET, "ARCLK", BigDecimal.valueOf(10), BigDecimal.valueOf(2), BigDecimal.valueOf(20));
        oldFilled.setCreatedAt(LocalDateTime.now().minusDays(10));

        when(orderRepository.findAllByCreatedAtDesc()).thenReturn(Arrays.asList(recentFilled, oldFilled));

        List<OrderResponse> list = orderService.getAllOrders();

        assertEquals(2, list.size());
        OrderResponse r1 = list.stream().filter(r -> Objects.equals(r.id(), 1L)).findFirst().orElseThrow();
        OrderResponse r2 = list.stream().filter(r -> Objects.equals(r.id(), 2L)).findFirst().orElseThrow();
        assertEquals(0, r1.blockedBalance().compareTo(BigDecimal.valueOf(20)));
        assertEquals(0, r2.blockedBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void whenGetOwnedStockQuantity_thenNetBuyMinusSellReturned() {
        Order b1 = newOrder(1L, 9L, OrderStatus.FILLED, OrderType.BUY, OrderCategory.MARKET, "ARCLK", BigDecimal.TEN, BigDecimal.valueOf(5), BigDecimal.valueOf(50));
        Order s1 = newOrder(2L, 9L, OrderStatus.FILLED, OrderType.SELL, OrderCategory.MARKET, "ARCLK", BigDecimal.valueOf(12), BigDecimal.valueOf(2), BigDecimal.valueOf(24));

        when(orderRepository.findByCustomerIdAndStockCodeAndStatus(9L, "ARCLK", OrderStatus.FILLED))
                .thenReturn(Arrays.asList(b1, s1));

        BigDecimal net = orderService.getOwnedStockQuantity(9L, "ARCLK");

        assertEquals(BigDecimal.valueOf(3), net);
    }

    @Test
    void whenGetPortfolioByCustomerId_thenReturnPositiveNetWithAveragePrice() {
        Order b1 = newOrder(1L, 5L, OrderStatus.FILLED, OrderType.BUY, OrderCategory.MARKET, "ARCLK", BigDecimal.valueOf(10), BigDecimal.valueOf(3), BigDecimal.valueOf(30));
        Order b2 = newOrder(2L, 5L, OrderStatus.FILLED, OrderType.BUY, OrderCategory.MARKET, "ARCLK", BigDecimal.valueOf(20), BigDecimal.valueOf(2), BigDecimal.valueOf(40));
        Order s1 = newOrder(3L, 5L, OrderStatus.FILLED, OrderType.SELL, OrderCategory.MARKET, "ARCLK", BigDecimal.valueOf(25), BigDecimal.valueOf(1), BigDecimal.valueOf(25));

        when(orderRepository.findByCustomerIdAndStatus(5L, OrderStatus.FILLED)).thenReturn(Arrays.asList(b1, b2, s1));

        List<PortfolioStockQuantityResponse> resp = orderService.getPortfolioByCustomerId(5L);

        assertEquals(1, resp.size());
        PortfolioStockQuantityResponse r = resp.get(0);
        assertEquals("ARCLK", r.stockCode());
        assertEquals(BigDecimal.valueOf(4), r.netQuantity());
        assertEquals(new BigDecimal("17.50"), r.averagePrice());
    }

    @Test
    void whenGetOrdersByCustomerId_thenMappedListReturned() {
        Order o = newOrder(1L, 6L, OrderStatus.FILLED, OrderType.BUY, OrderCategory.MARKET, "ARCLK", BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN);

        when(orderRepository.findByCustomerId(6L)).thenReturn(singletonList(o));
        when(orderMapper.toResponse(o)).thenReturn(
                new OrderResponse(1L, 6L, OrderCategory.MARKET, OrderType.BUY, OrderStatus.FILLED, "ARCLK",
                        BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        List<OrderResponse> list = orderService.getOrdersByCustomerId(6L);

        assertEquals(1, list.size());
        assertEquals(6L, list.get(0).customerId());
    }

    @Test
    void whenGetLastFiveOrders_thenMappedRecentResponsesReturned() {
        Order o = newOrder(1L, 3L, OrderStatus.FILLED, OrderType.SELL, OrderCategory.MARKET, "VESBE", BigDecimal.valueOf(15), BigDecimal.valueOf(2), BigDecimal.valueOf(30));

        when(orderRepository.findLastFilledOrders(PageRequest.of(0, 5))).thenReturn(singletonList(o));
        RecentOrderResponse mockRecent = mock(RecentOrderResponse.class);
        when(orderMapper.toRecentResponse(o)).thenReturn(mockRecent);

        List<RecentOrderResponse> list = orderService.getLastFiveOrders();

        assertEquals(1, list.size());
        assertSame(mockRecent, list.get(0));
    }

    @Test
    void whenGetAllFilledOrders_thenMappedListReturned() {
        Order o = newOrder(7L, 2L, OrderStatus.FILLED, OrderType.BUY, OrderCategory.MARKET, "ARCLK", BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN);

        when(orderRepository.findByStatus(OrderStatus.FILLED)).thenReturn(singletonList(o));
        when(orderMapper.toResponse(o)).thenReturn(
                new OrderResponse(7L, 2L, OrderCategory.MARKET, OrderType.BUY, OrderStatus.FILLED, "ARCLK",
                        BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        List<OrderResponse> list = orderService.getAllFilledOrders();

        assertEquals(1, list.size());
        assertEquals(OrderStatus.FILLED, list.get(0).status());
    }

    @Test
    void whenGetTodayFilledOrders_thenFilteredByDateRangeAndMapped() {
        Order o = newOrder(8L, 2L, OrderStatus.FILLED, OrderType.BUY, OrderCategory.MARKET, "ARCLK", BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN);

        when(orderRepository.findFilledOrdersToday(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(singletonList(o));
        when(orderMapper.toResponse(o)).thenReturn(
                new OrderResponse(8L, 2L, OrderCategory.MARKET, OrderType.BUY, OrderStatus.FILLED, "ARCLK",
                        BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, LocalDateTime.now(), null, null, null, BigDecimal.ZERO)
        );

        List<OrderResponse> list = orderService.getTodayFilledOrders();

        assertEquals(1, list.size());
    }

    @Test
    void whenGetTodayTotalTradeVolume_thenReturnSum() {
        when(orderRepository.getTodayTotalVolume(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(BigDecimal.valueOf(123.45));

        BigDecimal v = orderService.getTodayTotalTradeVolume();

        assertEquals(0, v.compareTo(BigDecimal.valueOf(123.45)));
    }

    @Test
    void whenGetPopularStockCodes_thenTop10Returned() {
        when(orderRepository.findPopularStockCodes(any(Pageable.class)))
                .thenReturn(Arrays.asList("ARCLK", "VESBE"));

        List<PopularStockCodesResponse> list = orderService.getPopularStockCodes();

        assertEquals(2, list.size());
        assertEquals("ARCLK", list.get(0).stockCode());
    }

    @Test
    void whenGetTotalTradeVolume_thenReturnSum() {
        when(orderRepository.getTotalTradeVolume()).thenReturn(BigDecimal.valueOf(9999));

        BigDecimal v = orderService.getTotalTradeVolume();

        assertEquals(BigDecimal.valueOf(9999), v);
    }

    @Test
    void whenGetTodayOrderCount_thenReturnCount() {
        when(orderRepository.countTodayOrders(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(42L);

        Long c = orderService.getTodayOrderCount();

        assertEquals(42L, c);
    }
}