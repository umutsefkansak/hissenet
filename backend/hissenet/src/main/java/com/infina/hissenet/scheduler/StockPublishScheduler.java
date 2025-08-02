package com.infina.hissenet.scheduler;

import com.infina.hissenet.service.abstracts.IMarketDataService;
import com.infina.hissenet.websocket.StockWebSocketPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockPublishScheduler {
    private final IMarketDataService marketService;
    private final StockWebSocketPublisher publisher;

    public StockPublishScheduler(IMarketDataService marketService, StockWebSocketPublisher publisher) {
        this.marketService = marketService;
        this.publisher = publisher;
    }

    @Scheduled(
            fixedRateString    = "${stock.scheduler.publish.rate}",
            initialDelayString = "${stock.scheduler.publish.initial-delay}"
    )
    public void runPublish() {
        //publisher.publish(marketService.getAllStocks());
    }
}