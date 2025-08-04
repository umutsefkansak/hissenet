package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.StockTransactionCreateRequest;
import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.entity.Stock;
import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.exception.order.OrderNotFoundException;
import com.infina.hissenet.exception.stock.StockNotFoundException;
import com.infina.hissenet.mapper.StockTransactionMapper;
import com.infina.hissenet.repository.StockTransactionRepository;
import com.infina.hissenet.service.abstracts.IStockTransactionService;
import com.infina.hissenet.utils.GenericServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockTransactionService extends GenericServiceImpl<StockTransaction, Long> implements IStockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final PortfolioService portfolioService;
    private final StockService stockService;
    private final OrderService orderService;
    private final StockTransactionMapper stockTransactionMapper;

    public StockTransactionService(JpaRepository<StockTransaction, Long> repository, StockTransactionRepository stockTransactionRepository, PortfolioService portfolioService, StockService stockService, OrderService orderService, StockTransactionMapper stockTransactionMapper) {
        super(repository);
        this.stockTransactionRepository = stockTransactionRepository;
        this.portfolioService = portfolioService;
        this.stockService = stockService;
        this.orderService = orderService;
        this.stockTransactionMapper = stockTransactionMapper;
    }

    // order emri üzerine stocktransaction oluşturma
    @Transactional
    public StockTransactionResponse createTransactionFromOrder(StockTransactionCreateRequest request) {
        Portfolio portfolio = findPortfolioOrThrow(request.portfolioId());
        Stock stock = findStockOrThrow(request.stockId());
        Order order = findOrderOrThrow(request.orderId());

        StockTransaction transaction = stockTransactionMapper.toEntity(request, portfolio, stock, order);

        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }
        if (transaction.getSettlementDate() == null) {
            transaction.setSettlementDate(LocalDateTime.now().plusDays(2)); // T+2
        }

        save(transaction);
        portfolioService.updatePortfolioValues(portfolio.getId());

        return stockTransactionMapper.toResponse(save(transaction));
    }
    // temmettü işlemi
    @Transactional
    public StockTransactionResponse createDividendTransaction(StockTransactionCreateRequest request) {
        Portfolio portfolio = findPortfolioOrThrow(request.portfolioId());
        Stock stock = findStockOrThrow(request.stockId());

        StockTransaction transaction = stockTransactionMapper.toEntity(request, portfolio, stock, null);

        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }
        if (transaction.getSettlementDate() == null) {
            transaction.setSettlementDate(LocalDateTime.now().plusDays(1)); // Temettü için T+1
        }

        save(transaction);
        portfolioService.updatePortfolioValues(portfolio.getId());

        return stockTransactionMapper.toResponse(stockTransactionRepository.save(transaction));
    }
    
    // Belirli bir portföye ait tüm işlemleri getirir - Join fetch ile
    public List<StockTransactionResponse> getTransactionsByPortfolioId(Long portfolioId) {
        return stockTransactionRepository.findByPortfolioIdWithJoins(portfolioId).stream()
                .map(stockTransactionMapper::toResponse)
                .toList();
    }
    
    // Belirli bir hisseye ait tüm işlemleri getirir - Join fetch ile
    public List<StockTransactionResponse> getTransactionsByStockId(Long stockId) {
        return stockTransactionRepository.findByStockIdWithJoins(stockId).stream()
                .map(stockTransactionMapper::toResponse)
                .toList();
    }
    
    // Belirli bir emire ait tüm işlemleri getirir - Join fetch ile
    public List<StockTransactionResponse> getTransactionsByOrderId(Long orderId) {
        return stockTransactionRepository.findByOrderIdWithJoins(orderId).stream()
                .map(stockTransactionMapper::toResponse)
                .toList();
    }
    
    // Belirtilen tarih aralığındaki işlemleri getirir - Join fetch ile
    public List<StockTransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return stockTransactionRepository.findByTransactionDateBetweenWithJoins(startDate, endDate).stream()
                .map(stockTransactionMapper::toResponse)
                .toList();
    }
    
    // Belirli bir işlem türüne göre işlemleri getirir - Join fetch ile
    public List<StockTransactionResponse> getTransactionsByType(String transactionType) {
        return stockTransactionRepository.findByTransactionTypeWithJoins(transactionType).stream()
                .map(stockTransactionMapper::toResponse)
                .toList();
    }

    // Tüm işlemleri join fetch ile getir
    public List<StockTransactionResponse> getAllTransactionsWithJoins() {
        return stockTransactionRepository.findAllWithJoins().stream()
                .map(stockTransactionMapper::toResponse)
                .toList();
    }

    private Portfolio findPortfolioOrThrow(Long id) {
        return portfolioService.getPortfolio(id);
    }
    /* Buralarda servise olması lazım hata da servisten dönmesi lazım */
    private Stock findStockOrThrow(Long id) {
        return stockService.findById(id)
                .orElseThrow(() -> new StockNotFoundException(id));
    }

    private Order findOrderOrThrow(Long id) {
        return orderService.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}