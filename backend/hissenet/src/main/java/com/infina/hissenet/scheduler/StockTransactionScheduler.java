package com.infina.hissenet.scheduler;

import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.service.StockTransactionService;
import com.infina.hissenet.service.abstracts.ICacheManagerService;
import com.infina.hissenet.service.abstracts.IPortfolioService;
import com.infina.hissenet.service.abstracts.IStockTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class StockTransactionScheduler {
    private static final Logger logger = LoggerFactory.getLogger(StockTransactionScheduler.class);

    private final IStockTransactionService stockTransactionService;
    private final IPortfolioService portfolioService;
    private final ICacheManagerService cacheManagerService;

    public StockTransactionScheduler(StockTransactionService stockTransactionService, IPortfolioService portfolioService, ICacheManagerService cacheManagerService) {
        this.stockTransactionService = stockTransactionService;
        this.portfolioService = portfolioService;
        this.cacheManagerService = cacheManagerService;
    }

    @Transactional
    @Scheduled(fixedDelay = 5 * 60 * 1000 + 1)
    public void changeCurrentPriceForStockTransaction() {
        logger.info("Starting stock transaction price update scheduler");

        List<StockTransaction> stockTransactions = stockTransactionService.findAll();
        Map<String, BigDecimal> stockCodeToPriceMap = new HashMap<>();

        for (StockTransaction transaction : stockTransactions) {
            try {
                String stockCode = transaction.getStockCode();
                BigDecimal price = stockCodeToPriceMap.computeIfAbsent(stockCode,
                        code -> cacheManagerService.getCachedByCode(code).lastPrice());
                transaction.setCurrentPrice(price);
            } catch (Exception e) {
                logger.error("Failed to update price for transaction ID: {}", transaction.getId(), e);
            }
        }


        int batchSize = 100;
        for (int i = 0; i < stockTransactions.size(); i += batchSize) {
            int end = Math.min(i + batchSize, stockTransactions.size());
            List<StockTransaction> batch = stockTransactions.subList(i, end);
            stockTransactionService.saveAll(batch);
        }
    }
}