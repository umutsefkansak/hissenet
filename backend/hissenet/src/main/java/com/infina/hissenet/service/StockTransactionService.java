package com.infina.hissenet.service;

import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.entity.enums.OrderType;
import com.infina.hissenet.entity.enums.StockTransactionType;
import com.infina.hissenet.entity.enums.TransactionStatus;
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
import java.util.List;

@Service
public class StockTransactionService extends GenericServiceImpl<StockTransaction, Long> implements IStockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final PortfolioService portfolioService;
    private final ICacheManagerService cacheManagerService;

    public StockTransactionService(JpaRepository<StockTransaction, Long> repository, StockTransactionRepository stockTransactionRepository, PortfolioService portfolioService, ICacheManagerService cacheManagerService) {
        super(repository);
        this.stockTransactionRepository = stockTransactionRepository;
        this.portfolioService = portfolioService;
        this.cacheManagerService = cacheManagerService;
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
        transaction.setSettlementDate(LocalDateTime.now().plusDays(2));
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
}