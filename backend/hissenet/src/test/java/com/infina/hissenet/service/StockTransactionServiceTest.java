package com.infina.hissenet.service;

import com.infina.hissenet.dto.response.CombinedStockData;
import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.entity.enums.OrderCategory;
import com.infina.hissenet.entity.enums.OrderStatus;
import com.infina.hissenet.entity.enums.OrderType;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.exception.transaction.UnauthorizedOperationException;
import com.infina.hissenet.exception.transaction.InsufficientStockException;
import com.infina.hissenet.repository.StockTransactionRepository;
import com.infina.hissenet.service.abstracts.ICacheManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockTransactionServiceTest {


    @Mock private JpaRepository<StockTransaction, Long> jpaRepository;
    @Mock private StockTransactionRepository stockTransactionRepository;
    @Mock private PortfolioService portfolioService;
    @Mock private ICacheManagerService cacheManagerService;
    @Mock private CustomerService customerService;
    @Mock private CommonFinancialService commonFinancialService;

    @InjectMocks private StockTransactionService service;

    private Customer customer;
    private Portfolio portfolio;

    @Captor private ArgumentCaptor<StockTransaction> txCaptor;

    @BeforeEach
    void init() {
        customer = new Customer();
        customer.setId(1L);
        customer.setCommissionRate(new BigDecimal("0.0025"));

        portfolio = new Portfolio();
        portfolio.setId(10L);
        portfolio.setCustomer(customer);
    }

    @Test
    void createTransactionFromOrder_buy_success_savesTransactionAndUpdatesPortfolio() {
        // Arrange
        Order order = new Order();
        order.setCustomer(customer);
        order.setType(OrderType.BUY);
        order.setCategory(OrderCategory.LIMIT);
        order.setStatus(OrderStatus.FILLED);
        order.setStockCode("THYAO");
        order.setQuantity(new BigDecimal("150"));
        order.setPrice(new BigDecimal("20.50"));
        order.setTotalAmount(new BigDecimal("3075.00"));

        when(portfolioService.getCustomerFirstPortfolio(1L)).thenReturn(portfolio);
        when(cacheManagerService.getCachedByCode("THYAO")).thenReturn(new CombinedStockData(
                "THYAO", null, null, null, null, new BigDecimal("21.00"), null, null, null, null, null, null, null
        ));

        when(jpaRepository.save(any(StockTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.createTransactionFromOrder(order);

        // Assert
        verify(jpaRepository).save(txCaptor.capture());
        StockTransaction saved = txCaptor.getValue();
        assertEquals(portfolio, saved.getPortfolio());
        assertEquals("THYAO", saved.getStockCode());
        assertEquals(150, saved.getQuantity());
        assertEquals(order.getPrice(), saved.getPrice());
        assertEquals(order.getPrice(), saved.getExecutionPrice());
        assertEquals(new BigDecimal("3075.00"), saved.getTotalAmount());
        assertEquals(new BigDecimal("21.00"), saved.getCurrentPrice());
        assertEquals(TransactionStatus.COMPLETED, saved.getTransactionStatus());


        verify(portfolioService).updatePortfolioValues(portfolio.getId());
    }

    @Test
    void createTransactionFromOrder_sell_insufficientStock_throws() {
        // Arrange
        Order order = new Order();
        order.setCustomer(customer);
        order.setType(OrderType.SELL);
        order.setCategory(OrderCategory.MARKET);
        order.setStatus(OrderStatus.FILLED);
        order.setStockCode("THYAO");
        order.setQuantity(new BigDecimal("200"));
        order.setPrice(new BigDecimal("20.50"));
        order.setTotalAmount(new BigDecimal("4100.00"));

        when(portfolioService.getCustomerFirstPortfolio(1L)).thenReturn(portfolio);
        when(cacheManagerService.getCachedByCode("THYAO")).thenReturn(new CombinedStockData(
                "THYAO", null, null, null, null, new BigDecimal("21.00"), null, null, null, null, null, null, null
        ));
        when(commonFinancialService.getQuantityForStockTransactionWithStream(1L, "THYAO")).thenReturn(50);

        // Act + Assert
        assertThrows(InsufficientStockException.class, () -> service.createTransactionFromOrder(order));
        verify(jpaRepository, never()).save(any());
        verify(portfolioService, never()).updatePortfolioValues(anyLong());
    }

    @Test
    void getAllBuyTransactions_delegatesToCommonFinancialService_andUpdatesPortfolio() {
        // Arrange
        Long portfolioId = 10L;
        List<StockTransactionResponse> expected = List.of();
        when(commonFinancialService.getAllBuyTransactions(portfolioId)).thenReturn(expected);

        // Act
        List<StockTransactionResponse> result = service.getAllBuyTransactions(portfolioId);

        // Assert
        assertSame(expected, result);
        verify(portfolioService).updatePortfolioValues(portfolioId);
    }

    @Test
    void updatePortfolioIdForStockTransactions_success_updatesAndRecalculates() {
        // Arrange
        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setId(20L);
        oldPortfolio.setCustomer(customer);

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setId(30L);
        newPortfolio.setCustomer(customer);

        StockTransaction tx = new StockTransaction();
        tx.setId(100L);
        tx.setPortfolio(oldPortfolio);
        tx.setStockCode("GARAN");

        when(stockTransactionRepository.findById(100L)).thenReturn(Optional.of(tx));
        when(portfolioService.findById(30L)).thenReturn(Optional.of(newPortfolio));
        when(stockTransactionRepository.updatePortfolioIdByCustomerIdAndStockCode(30L, 1L, "GARAN")).thenReturn(3);

        // Act
        service.updatePortfolioIdForStockTransactions(100L, 30L);

        // Assert
        verify(stockTransactionRepository).updatePortfolioIdByCustomerIdAndStockCode(30L, 1L, "GARAN");
        verify(portfolioService).updatePortfolioValues(30L);
        verify(portfolioService).updatePortfolioValues(20L);
    }

    @Test
    void updatePortfolioIdForStockTransactions_differentCustomer_throwsUnauthorized() {
        // Arrange
        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setId(20L);
        oldPortfolio.setCustomer(customer);

        Customer other = new Customer();
        other.setId(2L);
        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setId(30L);
        newPortfolio.setCustomer(other);

        StockTransaction tx = new StockTransaction();
        tx.setId(100L);
        tx.setPortfolio(oldPortfolio);
        tx.setStockCode("GARAN");

        when(stockTransactionRepository.findById(100L)).thenReturn(Optional.of(tx));
        when(portfolioService.findById(30L)).thenReturn(Optional.of(newPortfolio));

        // Act + Assert
        assertThrows(UnauthorizedOperationException.class, () -> service.updatePortfolioIdForStockTransactions(100L, 30L));
        verify(stockTransactionRepository, never()).updatePortfolioIdByCustomerIdAndStockCode(anyLong(), anyLong(), anyString());
    }

    @Test
    void processStockSettlements_marksAsSettled_andPersists_andRecalculates() {
        // Arrange
        StockTransaction t1 = new StockTransaction();
        Portfolio p1 = new Portfolio(); p1.setId(11L); t1.setPortfolio(p1);
        StockTransaction t2 = new StockTransaction();
        Portfolio p2 = new Portfolio(); p2.setId(12L); t2.setPortfolio(p2);

        when(stockTransactionRepository.findStockTransactionsReadyForSettlement(any(LocalDateTime.class), any(), any(), any()))
                .thenReturn(List.of(t1, t2));
        when(jpaRepository.save(any(StockTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.processStockSettlements();

        // Assert
        verify(jpaRepository, times(2)).save(any(StockTransaction.class));
        verify(portfolioService).updatePortfolioValues(11L);
        verify(portfolioService).updatePortfolioValues(12L);
        assertEquals(TransactionStatus.SETTLED, t1.getTransactionStatus());
        assertEquals(TransactionStatus.SETTLED, t2.getTransactionStatus());
    }
}


