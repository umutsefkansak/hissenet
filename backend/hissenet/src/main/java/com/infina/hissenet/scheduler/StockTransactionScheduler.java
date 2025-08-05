package com.infina.hissenet.scheduler;

import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.service.PortfolioService;
import com.infina.hissenet.service.StockTransactionService;
import com.infina.hissenet.service.abstracts.ICacheManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Component
public class StockTransactionScheduler {
    private static final Logger logger = LoggerFactory.getLogger(StockTransactionScheduler.class);

    private final StockTransactionService stockTransactionService;
    private final PortfolioService portfolioService;
    private final ICacheManagerService cacheManagerService;

    public StockTransactionScheduler(StockTransactionService stockTransactionService, PortfolioService portfolioService, ICacheManagerService cacheManagerService) {
        this.stockTransactionService = stockTransactionService;
        this.portfolioService = portfolioService;
        this.cacheManagerService = cacheManagerService;
    }

    @Transactional
    @Scheduled(fixedDelay = 60000 * 5 + 1)
    public void changeCurrentPriceForStockTransaction() {
        logger.info("Starting stock transaction price update scheduler");
        List<StockTransaction> stockTransactions = stockTransactionService.findAll();
        for (StockTransaction stockTransaction : stockTransactions) {
            stockTransaction.setCurrentPrice(cacheManagerService.getCachedByCode(stockTransaction.getStockCode()).lastPrice());
        }
        stockTransactionService.saveAll(stockTransactions);

    }
}