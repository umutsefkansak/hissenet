package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.StockTransactionCreateRequest;
import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.entity.Stock;
import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.mapper.StockTransactionMapper;
import com.infina.hissenet.repository.OrderRepository;
import com.infina.hissenet.repository.StockRepository;
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
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final StockTransactionMapper stockTransactionMapper;

    public StockTransactionService(JpaRepository<StockTransaction, Long> repository, StockTransactionRepository stockTransactionRepository, PortfolioService portfolioService, StockRepository stockRepository, OrderRepository orderRepository, StockTransactionMapper stockTransactionMapper) {
        super(repository);
        this.stockTransactionRepository = stockTransactionRepository;
        this.portfolioService = portfolioService;
        this.stockRepository = stockRepository;
        this.orderRepository = orderRepository;
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

        return stockTransactionMapper.toResponse(stockTransactionRepository.save(transaction));
    }
    // Belirli bir portföye ait tüm işlemleri getirir.
    public List<StockTransactionResponse> getTransactionsByPortfolioId(Long portfolioId) {
        return stockTransactionRepository.findByPortfolioId(portfolioId).stream()
                .map(stockTransactionMapper::toResponse)
                .toList();
    }
    // Belirli bir hisseye ait tüm işlemleri getirir.
    public List<StockTransactionResponse> getTransactionsByStockId(Long stockId) {
        return stockTransactionRepository.findByStockId(stockId).stream()
                .map(stockTransactionMapper::toResponse)
                .toList();
    }
    // Belirli bir emire ait tüm işlemleri getirir.
    public List<StockTransactionResponse> getTransactionsByOrderId(Long orderId) {
        return stockTransactionRepository.findByOrderId(orderId).stream()
                .map(stockTransactionMapper::toResponse)
                .toList();
    }
    // Belirtilen tarih aralığındaki işlemleri getirir.
    public List<StockTransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return stockTransactionRepository.findByTransactionDateBetween(startDate, endDate).stream()
                .map(stockTransactionMapper::toResponse)
                .toList();
    }
    // Belirli bir işlem türüne göre işlemleri getirir.
    public List<StockTransactionResponse> getTransactionsByType(String transactionType) {
        return stockTransactionRepository.findByTransactionType(transactionType).stream()
                .map(stockTransactionMapper::toResponse)
                .toList();
    }



    private Portfolio findPortfolioOrThrow(Long id) {
        return portfolioService.getPortfolio(id);
    }
    /* Buralarda servise olması lazım hata da servisten dönmesi lazım */
    private Stock findStockOrThrow(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Stock bulunamadı: " + id));
    }

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order bulunamadı: " + id));
    }
}