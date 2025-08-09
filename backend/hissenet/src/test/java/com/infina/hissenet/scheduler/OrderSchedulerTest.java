package com.infina.hissenet.scheduler;

import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.enums.OrderStatus;
import com.infina.hissenet.entity.enums.OrderType;
import com.infina.hissenet.repository.OrderRepository;
import com.infina.hissenet.service.MarketHourService;
import com.infina.hissenet.service.abstracts.ICacheManagerService;
import com.infina.hissenet.service.abstracts.IStockTransactionService;
import com.infina.hissenet.service.abstracts.IWalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderSchedulerTest {

    @Mock private OrderRepository orderRepository;
    @Mock private IWalletService walletService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private ICacheManagerService stockCacheService;
    @Mock private IStockTransactionService stockTransactionService;
    @Mock private MarketHourService marketHourService;

    @InjectMocks private OrderScheduler scheduler;

    @Captor private ArgumentCaptor<Order> orderCaptor;

    private static Order newOpenOrder(Long customerId, String stock, OrderType type, BigDecimal limit, BigDecimal qty) {
        Customer c = new Customer();
        c.setId(customerId);
        Order o = new Order();
        o.setCustomer(c);
        o.setStockCode(stock);
        o.setType(type);
        o.setPrice(limit);
        o.setQuantity(qty);
        o.setStatus(OrderStatus.OPEN);
        return o;
    }

    @Test
    void whenMarketPriceLeqLimit_andBuyOrderOpen_thenWalletPurchaseAndFilled() {
        Order buy = newOpenOrder(10L, "ARCLK", OrderType.BUY, new BigDecimal("30"), new BigDecimal("2"));
        when(orderRepository.findByStatus(OrderStatus.OPEN)).thenReturn(List.of(buy));
        when(stockCacheService.getCachedByCode("ARCLK").lastPrice()).thenReturn(new BigDecimal("25"));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        scheduler.processPendingLimitOrders();

        verify(walletService).processStockPurchase(eq(10L),
                argThat(bd -> bd.compareTo(new BigDecimal("60")) == 0),
                any());
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.FILLED, orderCaptor.getValue().getStatus());
        verify(stockTransactionService).createTransactionFromOrder(orderCaptor.getValue());
    }

    @Test
    void whenMarketPriceGeqLimit_andSellOrderOpen_thenWalletSaleAndFilled() {
        Order sell = newOpenOrder(20L, "VESBE", OrderType.SELL, new BigDecimal("20"), new BigDecimal("3"));
        when(orderRepository.findByStatus(OrderStatus.OPEN)).thenReturn(List.of(sell));
        when(stockCacheService.getCachedByCode("VESBE").lastPrice()).thenReturn(new BigDecimal("25"));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        scheduler.processPendingLimitOrders();

        verify(walletService).processStockSale(eq(20L),
                argThat(bd -> bd.compareTo(new BigDecimal("60")) == 0),
                any());
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.FILLED, orderCaptor.getValue().getStatus());
        verify(stockTransactionService).createTransactionFromOrder(orderCaptor.getValue());
    }

    @Test
    void whenPriceNotMet_thenKeepOpen_andNoWalletOrTransaction() {
        Order buy = newOpenOrder(30L, "THYAO", OrderType.BUY, new BigDecimal("20"), new BigDecimal("5"));
        when(orderRepository.findByStatus(OrderStatus.OPEN)).thenReturn(List.of(buy));
        when(stockCacheService.getCachedByCode("THYAO").lastPrice()).thenReturn(new BigDecimal("25"));

        scheduler.processPendingLimitOrders();

        verifyNoInteractions(walletService);
        verify(orderRepository, never()).save(any());
        verify(stockTransactionService, never()).createTransactionFromOrder(any());
        assertEquals(OrderStatus.OPEN, buy.getStatus());
    }

    @Test
    void whenMarketPriceIsNull_thenSkipNoChanges() {
        Order buy = newOpenOrder(40L, "ASELS", OrderType.BUY, new BigDecimal("50"), new BigDecimal("1"));
        when(orderRepository.findByStatus(OrderStatus.OPEN)).thenReturn(List.of(buy));
        when(stockCacheService.getCachedByCode("ASELS").lastPrice()).thenReturn(null);

        scheduler.processPendingLimitOrders();

        verifyNoInteractions(walletService);
        verify(orderRepository, never()).save(any());
        verify(stockTransactionService, never()).createTransactionFromOrder(any());
        assertEquals(OrderStatus.OPEN, buy.getStatus());
    }

    @Test
    void whenWalletThrows_thenStatusFailed_andSaved_andTransactionCreated() {
        Order buy = newOpenOrder(50L, "KRDMD", OrderType.BUY, new BigDecimal("10"), new BigDecimal("2"));
        when(orderRepository.findByStatus(OrderStatus.OPEN)).thenReturn(List.of(buy));
        when(stockCacheService.getCachedByCode("KRDMD").lastPrice()).thenReturn(new BigDecimal("10"));

        doThrow(new RuntimeException("wallet err"))
                .when(walletService).processStockPurchase(eq(50L),
                        argThat(bd -> bd.compareTo(new BigDecimal("20")) == 0),
                        any());
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        scheduler.processPendingLimitOrders();

        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.FAILED, orderCaptor.getValue().getStatus());
        verify(stockTransactionService).createTransactionFromOrder(orderCaptor.getValue());
    }

    @Test
    void whenMultipleOrdersMixed_thenOnlyEligibleFilled_andOthersUnchanged() {
        Order buyFill   = newOpenOrder(70L, "TUPRS", OrderType.BUY,  new BigDecimal("20"), new BigDecimal("2"));
        Order sellKeep  = newOpenOrder(80L, "SISE",  OrderType.SELL, new BigDecimal("40"), new BigDecimal("1"));
        Order sellFill  = newOpenOrder(90L, "BIMAS", OrderType.SELL, new BigDecimal("30"), new BigDecimal("4"));
        Order priceNull = newOpenOrder(91L, "XNULL", OrderType.BUY,  new BigDecimal("10"), new BigDecimal("1"));

        when(orderRepository.findByStatus(OrderStatus.OPEN)).thenReturn(List.of(buyFill, sellKeep, sellFill, priceNull));

        when(stockCacheService.getCachedByCode("TUPRS").lastPrice()).thenReturn(new BigDecimal("20"));
        when(stockCacheService.getCachedByCode("SISE").lastPrice()).thenReturn(new BigDecimal("35"));
        when(stockCacheService.getCachedByCode("BIMAS").lastPrice()).thenReturn(new BigDecimal("31"));
        when(stockCacheService.getCachedByCode("XNULL").lastPrice()).thenReturn(null);

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        scheduler.processPendingLimitOrders();

        verify(walletService).processStockPurchase(eq(70L),
                argThat(bd -> bd.compareTo(new BigDecimal("40")) == 0),
                any());
        verify(walletService).processStockSale(eq(90L),
                argThat(bd -> bd.compareTo(new BigDecimal("120")) == 0),
                any());
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(stockTransactionService, times(2)).createTransactionFromOrder(any(Order.class));

        assertEquals(OrderStatus.OPEN, sellKeep.getStatus());
        assertEquals(OrderStatus.OPEN, priceNull.getStatus());
    }
}