package com.infina.hissenet.scheduler;

import com.infina.hissenet.properties.StockProperties;
import com.infina.hissenet.service.BorsaIstanbulCacheService;
import com.infina.hissenet.service.CombinedCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheRefreshScheduler {

    private static final Logger log = LoggerFactory.getLogger(CacheRefreshScheduler.class);

    private final CombinedCacheService combined;
    private final BorsaIstanbulCacheService bist;
    private final SimpMessagingTemplate ws;
    private final StockProperties stockProps;

    public CacheRefreshScheduler(CombinedCacheService combined, BorsaIstanbulCacheService bist, SimpMessagingTemplate ws, StockProperties stockProps) {
        this.combined = combined;
        this.bist = bist;
        this.ws = ws;
        this.stockProps = stockProps;
    }

    @Scheduled(fixedRateString = "${stock.scheduler.refresh.rate}", initialDelayString = "${stock.scheduler.refresh.initial-delay}")
    public void refreshAll() {
        combined.refreshAsync()
                .doOnTerminate(() -> {
                    var list = combined.getAll();
                    if (!list.isEmpty()) {
                        ws.convertAndSend(stockProps.getScheduler().getPublish().getTopic(), list);
                    }
                })
                .subscribe();

        bist.refreshAsync()
                .doOnTerminate(() -> {
                    var bistList = bist.getAll();
                    if (!bistList.isEmpty()) {
                        ws.convertAndSend(
                                stockProps.getScheduler().getBorsaIstanbulPublish().getTopic(),
                                bistList.get(0)
                        );
                    }
                })
                .subscribe();
    }
}
