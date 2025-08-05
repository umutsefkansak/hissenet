package com.infina.hissenet.scheduler;

import com.infina.hissenet.dto.response.CombinedStockData;
import com.infina.hissenet.properties.StockProperties;
import com.infina.hissenet.service.CombinedCacheService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CombinedPublishScheduler {
    private final CombinedCacheService combinedCacheService;
    private final SimpMessagingTemplate  ws;
    private final String  topic;


    public CombinedPublishScheduler(CombinedCacheService combinedCacheService,
                                    SimpMessagingTemplate ws,
                                    StockProperties stockProperties) {
        this.combinedCacheService = combinedCacheService;
        this.ws                   = ws;

        this.topic                = stockProperties
                .getScheduler()
                .getPublish()
                .getTopic();
    }

    @Scheduled(
            fixedRateString    = "${stock.scheduler.publish.rate}",
            initialDelayString = "${stock.scheduler.publish.initial-delay}"
    )
    public void run() {
        // 1) Tüm birleşik listeyi cache’den al
        List<CombinedStockData> combinedList = combinedCacheService.getAllCombined();

        // 2) Opsiyonel log
        System.out.println("\n===== Pushing combinedList =====");
        combinedList.forEach(System.out::println);

        // 3) WebSocket ile yayınla
        ws.convertAndSend(topic, combinedList);
    }
}