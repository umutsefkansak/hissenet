package com.infina.hissenet.service;

import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.entity.enums.MarketOrderType;
import com.infina.hissenet.entity.enums.StockTransactionType;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import com.infina.hissenet.mapper.StockTransactionMapper;
import com.infina.hissenet.repository.StockTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommonFinancialService Unit Tests")
class CommonFinancialServiceTest {

    @Mock
    private StockTransactionRepository stockTransactionRepository;

    @Mock
    private StockTransactionMapper stockTransactionMapper;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CommonFinancialService commonFinancialService;

    private Customer testCustomer;
    private Portfolio testPortfolio;
    private StockTransaction buyTransaction1;
    private StockTransaction buyTransaction2;
    private StockTransaction sellTransaction;
    private StockTransactionResponse mockResponse;

    @BeforeEach
    void setUp() {
        // Arrange - Test data setup
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setEmail("test@example.com");
        testCustomer.setCustomerNumber("CUST001");

        testPortfolio = new Portfolio();
        testPortfolio.setId(1L);
        testPortfolio.setCustomer(testCustomer);
        testPortfolio.setPortfolioName("Test Portfolio");


        buyTransaction1 = createStockTransaction(1L, "THYAO", StockTransactionType.BUY, TransactionStatus.SETTLED, 100, new BigDecimal("50.00"));
        buyTransaction2 = createStockTransaction(2L, "THYAO", StockTransactionType.BUY, TransactionStatus.SETTLED, 50, new BigDecimal("55.00"));
        sellTransaction = createStockTransaction(3L, "THYAO", StockTransactionType.SELL, TransactionStatus.SETTLED, 30, new BigDecimal("60.00"));


        mockResponse = new StockTransactionResponse(
                1L, 1L, "Test Portfolio", "THYAO", 1L,
                StockTransactionType.BUY, TransactionStatus.SETTLED,
                150, new BigDecimal("52.50"), new BigDecimal("7875.00"),
                new BigDecimal("39.38"), new BigDecimal("0.00"), new BigDecimal("0.00"),
                MarketOrderType.MARKET, null, new BigDecimal("52.50"),
                new BigDecimal("55.00"), LocalDateTime.now(), LocalDateTime.now(),
                "Test transaction", LocalDateTime.now(), LocalDateTime.now()
        );
    }


    @Test
    @DisplayName("getAllBuyTransactions - Should return empty list when no BUY transactions")
    void getAllBuyTransactions_ShouldReturnEmptyList_WhenNoBuyTransactions() {
        // Arrange
        Long portfolioId = 1L;
        List<StockTransaction> transactions = Arrays.asList(sellTransaction);
        
        when(stockTransactionRepository.findByPortfolioId(portfolioId)).thenReturn(transactions);

        // Act
        List<StockTransactionResponse> result = commonFinancialService.getAllBuyTransactions(portfolioId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(stockTransactionRepository).findByPortfolioId(portfolioId);
        verify(stockTransactionMapper, never()).toResponse(any(StockTransaction.class));
    }




    @Test
    @DisplayName("mergeTransactions - Should throw exception when transactions list is null")
    void mergeTransactions_ShouldThrowException_WhenTransactionsListIsNull() {
        // Arrange
        List<StockTransaction> transactions = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            commonFinancialService.mergeTransactions(transactions);
        });
    }

    @Test
    @DisplayName("mergeTransactions - Should throw exception when transactions list is empty")
    void mergeTransactions_ShouldThrowException_WhenTransactionsListIsEmpty() {
        // Arrange
        List<StockTransaction> transactions = Collections.emptyList();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            commonFinancialService.mergeTransactions(transactions);
        });
    }



    @Test
    @DisplayName("getQuantityForStockTransactionWithStream - Should return correct net quantity")
    void getQuantityForStockTransactionWithStream_ShouldReturnCorrectNetQuantity() {
        // Arrange
        Long customerId = 1L;
        String stockCode = "THYAO";
        

        Portfolio portfolio1 = new Portfolio();
        portfolio1.setId(1L);
        portfolio1.setCustomer(testCustomer);
        
        Portfolio portfolio2 = new Portfolio();
        portfolio2.setId(2L);
        portfolio2.setCustomer(testCustomer);
        
        testCustomer.setPortfolios(Arrays.asList(portfolio1, portfolio2));
        

        StockTransaction buy1 = createStockTransaction(1L, stockCode, StockTransactionType.BUY, TransactionStatus.SETTLED, 100, new BigDecimal("50.00"));
        StockTransaction buy2 = createStockTransaction(2L, stockCode, StockTransactionType.BUY, TransactionStatus.SETTLED, 50, new BigDecimal("55.00"));
        StockTransaction sell1 = createStockTransaction(3L, stockCode, StockTransactionType.SELL, TransactionStatus.SETTLED, 30, new BigDecimal("60.00"));
        
        portfolio1.setTransactions(Arrays.asList(buy1, sell1));
        portfolio2.setTransactions(Arrays.asList(buy2));
        
        when(customerService.findById(customerId)).thenReturn(Optional.of(testCustomer));

        // Act
        Integer result = commonFinancialService.getQuantityForStockTransactionWithStream(customerId, stockCode);

        // Assert
        assertEquals(120, result); // 100 + 50 - 30 = 120
        verify(customerService).findById(customerId);
    }

    @Test
    @DisplayName("getQuantityForStockTransactionWithStream - Should return 0 when no transactions")
    void getQuantityForStockTransactionWithStream_ShouldReturnZero_WhenNoTransactions() {
        // Arrange
        Long customerId = 1L;
        String stockCode = "THYAO";
        
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setCustomer(testCustomer);
        portfolio.setTransactions(Collections.emptyList());
        
        testCustomer.setPortfolios(Arrays.asList(portfolio));
        
        when(customerService.findById(customerId)).thenReturn(Optional.of(testCustomer));

        // Act
        Integer result = commonFinancialService.getQuantityForStockTransactionWithStream(customerId, stockCode);

        // Assert
        assertEquals(0, result);
        verify(customerService).findById(customerId);
    }

    @Test
    @DisplayName("getQuantityForStockTransactionWithStream - Should throw exception when customer not found")
    void getQuantityForStockTransactionWithStream_ShouldThrowException_WhenCustomerNotFound() {
        // Arrange
        Long customerId = 999L;
        String stockCode = "THYAO";
        
        when(customerService.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            commonFinancialService.getQuantityForStockTransactionWithStream(customerId, stockCode);
        });
        verify(customerService).findById(customerId);
    }

    @Test
    @DisplayName("getQuantityForStockTransactionWithStream - Should handle PARTIALLY_SOLD transactions")
    void getQuantityForStockTransactionWithStream_ShouldHandlePartiallySoldTransactions() {
        // Arrange
        Long customerId = 1L;
        String stockCode = "THYAO";
        
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setCustomer(testCustomer);
        
        StockTransaction buyTransaction = createStockTransaction(1L, stockCode, StockTransactionType.BUY, TransactionStatus.PARTIALLY_SOLD, 100, new BigDecimal("50.00"));
        StockTransaction sellTransaction = createStockTransaction(2L, stockCode, StockTransactionType.SELL, TransactionStatus.PARTIALLY_SOLD, 30, new BigDecimal("60.00"));
        
        portfolio.setTransactions(Arrays.asList(buyTransaction, sellTransaction));
        testCustomer.setPortfolios(Arrays.asList(portfolio));
        
        when(customerService.findById(customerId)).thenReturn(Optional.of(testCustomer));

        // Act
        Integer result = commonFinancialService.getQuantityForStockTransactionWithStream(customerId, stockCode);

        // Assert
        assertEquals(70, result); // 100 - 30 = 70
        verify(customerService).findById(customerId);
    }

    @Test
    @DisplayName("getQuantityForStockTransactionWithStream - Should handle multiple portfolios")
    void getQuantityForStockTransactionWithStream_ShouldHandleMultiplePortfolios() {
        // Arrange
        Long customerId = 1L;
        String stockCode = "THYAO";
        
        Portfolio portfolio1 = new Portfolio();
        portfolio1.setId(1L);
        portfolio1.setCustomer(testCustomer);
        
        Portfolio portfolio2 = new Portfolio();
        portfolio2.setId(2L);
        portfolio2.setCustomer(testCustomer);
        
        StockTransaction buy1 = createStockTransaction(1L, stockCode, StockTransactionType.BUY, TransactionStatus.SETTLED, 100, new BigDecimal("50.00"));
        StockTransaction buy2 = createStockTransaction(2L, stockCode, StockTransactionType.BUY, TransactionStatus.SETTLED, 50, new BigDecimal("55.00"));
        StockTransaction sell1 = createStockTransaction(3L, stockCode, StockTransactionType.SELL, TransactionStatus.SETTLED, 30, new BigDecimal("60.00"));
        
        portfolio1.setTransactions(Arrays.asList(buy1, sell1));
        portfolio2.setTransactions(Arrays.asList(buy2));
        
        testCustomer.setPortfolios(Arrays.asList(portfolio1, portfolio2));
        
        when(customerService.findById(customerId)).thenReturn(Optional.of(testCustomer));

        // Act
        Integer result = commonFinancialService.getQuantityForStockTransactionWithStream(customerId, stockCode);

        // Assert
        assertEquals(120, result); // 100 + 50 - 30 = 120
        verify(customerService).findById(customerId);
    }

    private StockTransaction createStockTransaction(Long id, String stockCode, StockTransactionType type, 
                                                   TransactionStatus status, Integer quantity, BigDecimal price) {
        StockTransaction transaction = new StockTransaction();
        transaction.setId(id);
        transaction.setPortfolio(testPortfolio);
        transaction.setStockCode(stockCode);
        transaction.setTransactionType(type);
        transaction.setTransactionStatus(status);
        transaction.setQuantity(quantity);
        transaction.setPrice(price);
        transaction.setTotalAmount(price != null ? price.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO);
        transaction.setCommission(price != null ? BigDecimal.valueOf(quantity).multiply(price).multiply(new BigDecimal("0.001")) : BigDecimal.ZERO);
        transaction.setTax(BigDecimal.ZERO);
        transaction.setOtherFees(BigDecimal.ZERO);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setSettlementDate(LocalDateTime.now().plusDays(2));
        transaction.setMarketOrderType(MarketOrderType.MARKET);
        transaction.setExecutionPrice(price);
        transaction.setCurrentPrice(price);
        transaction.setNotes("Test transaction");
        return transaction;
    }
} 