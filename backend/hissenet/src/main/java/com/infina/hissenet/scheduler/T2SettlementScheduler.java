package com.infina.hissenet.scheduler;

import com.infina.hissenet.service.WalletService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class T2SettlementScheduler {
    private final WalletService walletService;

    public T2SettlementScheduler(WalletService walletService) {
        this.walletService = walletService;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void processT2Settlements() {
        walletService.processT2Settlements();
    }
}
