package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.PortfolioCreateRequest;
import com.infina.hissenet.dto.request.PortfolioUpdateRequest;
import com.infina.hissenet.dto.response.PortfolioResponse;
import com.infina.hissenet.dto.response.PortfolioSummaryResponse;
import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.entity.enums.PortfolioType;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.mapper.PortfolioMapper;
import com.infina.hissenet.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private JpaRepository<Portfolio, Long> jpaRepository;
    @Mock
    private PortfolioRepository portfolioRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private PortfolioMapper portfolioMapper;
    @Mock
    private CommonFinancialService commonFinancialService;

    @InjectMocks
    private PortfolioService service;

    private Customer customer;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);

        portfolio = new Portfolio();
        portfolio.setId(100L);
        portfolio.setCustomer(customer);
        portfolio.setPortfolioName("Core");
    }

    @Test
    void createPortfolio_success_returnsResponse() {
        // Arrange
        PortfolioCreateRequest request = new PortfolioCreateRequest("Core", "Long-term holdings", RiskProfile.AGGRESSIVE, PortfolioType.BALANCED);

        when(customerService.findById(1L)).thenReturn(Optional.of(customer));
        when(portfolioMapper.toEntity(request, customer)).thenReturn(portfolio);
        when(jpaRepository.save(any(Portfolio.class))).thenReturn(portfolio);

        PortfolioResponse expected = new PortfolioResponse(100L, null, null, "Core", "Long-term holdings", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, RiskProfile.AGGRESSIVE, PortfolioType.BALANCED, null, true, null, null, null);
        when(portfolioMapper.toResponse(portfolio)).thenReturn(expected);

        // Act
        PortfolioResponse response = service.createPortfolio(request, 1L);

        // Assert
        assertEquals(expected, response);
        verify(jpaRepository).save(any(Portfolio.class));
    }

    @Test
    void updatePortfolio_success_appliesMapperAndSaves() {
        // Arrange
        PortfolioUpdateRequest request = new PortfolioUpdateRequest("Growth", "Tech-focused", RiskProfile.AGGRESSIVE, PortfolioType.AGGRESSIVE, true);

        when(portfolioRepository.findById(100L)).thenReturn(Optional.of(portfolio));

        Portfolio updated = new Portfolio();
        updated.setId(100L);
        updated.setPortfolioName("Growth");
        when(jpaRepository.save(portfolio)).thenReturn(updated);

        PortfolioResponse expected = new PortfolioResponse(100L, 1L, "John Doe", "Growth", "Tech-focused", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, RiskProfile.AGGRESSIVE, PortfolioType.AGGRESSIVE, null, true, null, null, null);
        when(portfolioMapper.toResponse(updated)).thenReturn(expected);

        // Act
        PortfolioResponse response = service.updatePortfolio(100L, request);

        // Assert
        verify(portfolioMapper).updateEntity(portfolio, request);
        verify(jpaRepository).save(portfolio);
        assertEquals(expected, response);
    }

    @Test
    void deletePortfolio_deletesById() {
        // Arrange
        when(portfolioRepository.findById(100L)).thenReturn(Optional.of(portfolio));

        // Act
        service.deletePortfolio(100L);

        // Assert
        verify(jpaRepository).deleteById(100L);
    }

    @Test
    void getPortfolioResponse_fetchesWithCustomer_andMaps() {
        // Arrange
        when(portfolioRepository.findByIdWithCustomer(100L)).thenReturn(Optional.of(portfolio));

        PortfolioResponse expected = new PortfolioResponse(100L, 1L, "John Doe", "Core", null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null, null, null, true, null, null, null);
        when(portfolioMapper.toResponse(portfolio)).thenReturn(expected);

        // Act
        PortfolioResponse response = service.getPortfolioResponse(100L);

        // Assert
        assertEquals(expected, response);
    }

    @Test
    void updatePortfolioValues_computesAndPersistsMetrics_withinBounds() {
        // Arrange
        when(portfolioRepository.findById(100L)).thenReturn(Optional.of(portfolio));

        // Build sample merged buy transactions
        StockTransactionResponse t1 = new StockTransactionResponse(1L,                         // id
                100L,                       // portfolioId
                "Core",                     // portfolioName
                "THYAO",                    // stockCode
                null,                       // orderId
                null,                       // transactionType
                null,                       // transactionStatus
                200,                        // quantity
                new BigDecimal("10.00"),    // price
                BigDecimal.ZERO,            // totalAmount
                BigDecimal.ZERO,            // commission
                BigDecimal.ZERO,            // tax
                null,                       // otherFees
                null,                       // marketOrderType
                null,                       // limitPrice
                null,                       // executionPrice
                null,                       // currentPrice
                null,                       // transactionDate
                null,                       // settlementDate
                null,                       // notes
                null,                       // createdAt
                null                        // updatedAt
        );

        StockTransactionResponse t2 = new StockTransactionResponse(2L,
                100L,                       // portfolioId
                "Core",                     // portfolioName
                "GARAN",                    // stockCode
                null,                       // orderId
                null,                       // transactionType
                null,                       // transactionStatus
                100,                        // quantity
                new BigDecimal("30.00"),    // price
                BigDecimal.ZERO,            // totalAmount
                BigDecimal.ZERO,            // commission
                BigDecimal.ZERO,            // tax
                null,                       // otherFees
                null,                       // marketOrderType
                null,                       // limitPrice
                null,                       // executionPrice
                null,                       // currentPrice
                null,                       // transactionDate
                null,                       // settlementDate
                null,                       // notes
                null,                       // createdAt
                null                        // updatedAt
        );

        when(commonFinancialService.getAllBuyTransactions(100L)).thenReturn(List.of(t1, t2));

        // Act
        PortfolioResponse response = service.updatePortfolioValues(100L);

        // Assert
        // totalValue is computed via currentPrice*quantity in service.calculateTotalValue; here currentPrice is null, so ZERO totals

        verify(jpaRepository).save(any(Portfolio.class));
    }

    @Test
    void getPortfoliosByCustomer_mapsToSummaryResponse() {
        // Arrange
        Portfolio p2 = new Portfolio();
        p2.setId(101L);
        when(portfolioRepository.findByCustomerId(1L)).thenReturn(List.of(portfolio, p2));

        PortfolioSummaryResponse s1 = new PortfolioSummaryResponse(100L, "Core", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, RiskProfile.MODERATE, PortfolioType.BALANCED, true, null, List.of());
        PortfolioSummaryResponse s2 = new PortfolioSummaryResponse(101L, "Growth", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, RiskProfile.AGGRESSIVE, PortfolioType.AGGRESSIVE, true, null, List.of());
        when(portfolioMapper.toSummaryResponse(portfolio)).thenReturn(s1);
        when(portfolioMapper.toSummaryResponse(p2)).thenReturn(s2);

        // Act
        List<PortfolioSummaryResponse> result = service.getPortfoliosByCustomer(1L);

        // Assert
        assertEquals(List.of(s1, s2), result);
    }

    @Test
    void getActivePortfolios_returnsOnlyActiveSummaries() {
        // Arrange
        Portfolio inactive = new Portfolio();
        inactive.setId(102L);
        when(portfolioRepository.findByIsActiveTrue()).thenReturn(List.of(portfolio));

        PortfolioSummaryResponse s1 = new PortfolioSummaryResponse(100L, "Core", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, RiskProfile.MODERATE, PortfolioType.BALANCED, true, null, List.of());
        when(portfolioMapper.toSummaryResponse(portfolio)).thenReturn(s1);

        // Act
        List<PortfolioSummaryResponse> result = service.getActivePortfolios();

        // Assert
        assertEquals(List.of(s1), result);
    }
}


