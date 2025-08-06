package com.infina.hissenet.service;

import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.entity.enums.OrderType;
import com.infina.hissenet.entity.enums.StockTransactionType;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.exception.transaction.UnauthorizedOperationException;
import com.infina.hissenet.mapper.StockTransactionMapper;
import com.infina.hissenet.repository.PortfolioRepository;
import com.infina.hissenet.repository.StockTransactionRepository;
import com.infina.hissenet.service.abstracts.ICacheManagerService;
import com.infina.hissenet.service.abstracts.IStockTransactionService;
import com.infina.hissenet.utils.GenericServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockTransactionService extends GenericServiceImpl<StockTransaction, Long> implements IStockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final PortfolioService portfolioService;
    private final ICacheManagerService cacheManagerService;
    private final StockTransactionMapper mapper;

    public StockTransactionService(JpaRepository<StockTransaction, Long> repository, StockTransactionRepository stockTransactionRepository, PortfolioService portfolioService, ICacheManagerService cacheManagerService, StockTransactionMapper mapper) {
        super(repository);
        this.stockTransactionRepository = stockTransactionRepository;
        this.portfolioService = portfolioService;
        this.cacheManagerService = cacheManagerService;
        this.mapper = mapper;
    }

    // Order oluştuğunda otomatik StockTransaction oluştur
    @Transactional
    public void createTransactionFromOrder(Order order) {

        Portfolio portfolio = portfolioService.getCustomerFirstPortfolio(order.getCustomer().getId());

        // StockTransaction oluştur
        StockTransaction transaction = new StockTransaction();
        transaction.setPortfolio(portfolio);
        transaction.setStockCode(order.getStockCode());
        transaction.setTransactionType(order.getType() == OrderType.BUY?
                StockTransactionType.BUY : StockTransactionType.SELL);
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        transaction.setQuantity(order.getQuantity().intValue());


        transaction.setPrice(order.getPrice());
        transaction.setExecutionPrice(order.getPrice());
        transaction.setTotalAmount(order.getTotalAmount());
        System.out.println();
        // Cache'den anlık fiyatı al
        BigDecimal currentPrice = cacheManagerService.getCachedByCode(order.getStockCode()).lastPrice();

        transaction.setCurrentPrice(currentPrice);
        transaction.setOrder(order);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setSettlementDate(LocalDateTime.now().plusMinutes(1));
        transaction.setNotes("Order üzerinden oluşturulan işlem");


        BigDecimal commissionRate = portfolio.getCustomer().getCommissionRate();
        if (commissionRate == null) {
            commissionRate = BigDecimal.valueOf(0.001);
        }
        BigDecimal commission = order.getTotalAmount().multiply(commissionRate);
        transaction.setCommission(commission);

        save(transaction);
        portfolioService.updatePortfolioValues(transaction.getPortfolio().getId());
    }

   @Override
   @Transactional
    public void saveAll(List<StockTransaction> stockTransactions) {
        stockTransactionRepository.saveAll(stockTransactions);
    }

    public List<StockTransactionResponse> getAllBuyTransactions(Long portfolioId) {
        List<StockTransaction> allTransactions = stockTransactionRepository.findByPortfolioId(portfolioId);

        List<StockTransaction> filteredTransactions = allTransactions.stream()
                .filter(tx -> tx.getTransactionType() == StockTransactionType.BUY)
                .filter(tx -> tx.getTransactionStatus() == TransactionStatus.SETTLED)
                .toList();

        Map<String, List<StockTransaction>> groupedByStockCode = filteredTransactions.stream()
                .collect(Collectors.groupingBy(StockTransaction::getStockCode));

        List<StockTransactionResponse> mergedResponses = new ArrayList<>(groupedByStockCode.size());

        for (Map.Entry<String, List<StockTransaction>> entry : groupedByStockCode.entrySet()) {
            mergedResponses.add(mergeTransactions(entry.getValue()));
        }

        return mergedResponses;
    }

    @Transactional
    public void processStockSettlements() {
        List<StockTransaction> transactionsReadyForSettlement = stockTransactionRepository
                .findStockTransactionsReadyForSettlement(
                        LocalDateTime.now(),
                        TransactionStatus.COMPLETED,
                        StockTransactionType.BUY,
                        StockTransactionType.SELL
                );
        System.out.println(transactionsReadyForSettlement.toString());

        for (StockTransaction transaction : transactionsReadyForSettlement) {
            processStockSettlement(transaction);
        }
    }

    private void processStockSettlement(StockTransaction transaction) {
        // Settlement işlemlerini burada yapabilirsin (gerekirse)
        // Örneğin portfolio güncellemeleri vb.

        transaction.setTransactionStatus(TransactionStatus.SETTLED);
        // Settlement tarihini güncelleyebilirsin veya ayrı bir actualSettlementDate alanı ekleyebilirsin

        save(transaction);

        // Portfolio değerlerini güncelle
        portfolioService.updatePortfolioValues(transaction.getPortfolio().getId());
    }

    private StockTransactionResponse mergeTransactions(List<StockTransaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Transactions list cannot be null or empty");
        }

        StockTransaction baseTx = transactions.get(0);
        StockTransactionResponse baseResponse = mapper.toResponse(baseTx);

        int totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalOtherFees = BigDecimal.ZERO;

        for (StockTransaction tx : transactions) {
            totalQuantity += tx.getQuantity();
            totalAmount = totalAmount.add(tx.getTotalAmount());
            totalCommission = totalCommission.add(tx.getCommission());
            totalTax = totalTax.add(tx.getTax());
            totalOtherFees = totalOtherFees.add(tx.getOtherFees());
        }

        return new StockTransactionResponse(
                baseResponse.id(),
                baseResponse.portfolioId(),
                baseResponse.portfolioName(),
                baseResponse.stockCode(),
                baseResponse.orderId(),
                baseResponse.transactionType(),
                baseResponse.transactionStatus(),
                totalQuantity,
                baseResponse.price(),
                totalAmount,
                totalCommission,
                totalTax,
                totalOtherFees,
                baseResponse.marketOrderType(),
                baseResponse.limitPrice(),
                baseResponse.executionPrice(),
                baseResponse.currentPrice(),
                baseResponse.transactionDate(),
                baseResponse.settlementDate(),
                baseResponse.notes(),
                baseResponse.createdAt(),
                baseResponse.updatedAt()
        );
    }
    public void updatePortfolioIdForStockTransactions(Long transactionId,Long portfolioId) {
        StockTransaction transaction = stockTransactionRepository.findById(transactionId).orElseThrow(()->new NotFoundException("Stock "));
        Portfolio portfolio=portfolioService.findById(portfolioId).orElseThrow(()->new NotFoundException("Portfolio "));
        if (!portfolio.getCustomer().getId().equals(transaction.getPortfolio().getCustomer().getId())) {
            throw new UnauthorizedOperationException("You are not authorized to modify this portfolio");
        }
        transaction.setPortfolio(portfolio);
        save(transaction);
    }

}