package com.infina.hissenet.scheduler;

import com.infina.hissenet.service.StockTransactionService;
import com.infina.hissenet.service.WalletService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class T2SettlementScheduler {
    private final WalletService walletService;
    private final StockTransactionService stockTransactionService;

    public T2SettlementScheduler(WalletService walletService, StockTransactionService stockTransactionService) {
        this.walletService = walletService;
        this.stockTransactionService = stockTransactionService;
    }

    @Scheduled(cron = "0 */1 * * * ?")// bu satır gercek hayattaki kullanım  @Scheduled(cron = "0 5 17 * * MON-FRI")
    public void processT2Settlements() {
        stockTransactionService.processStockSettlements();
        walletService.processT2Settlements();
    }
}
