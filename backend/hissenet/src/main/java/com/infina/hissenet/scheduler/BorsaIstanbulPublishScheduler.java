package com.infina.hissenet.scheduler;

import com.infina.hissenet.client.CollectApiClient;
import com.infina.hissenet.properties.StockProperties;
import com.infina.hissenet.service.BorsaIstanbulCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class BorsaIstanbulPublishScheduler {
    private static final Logger log = LoggerFactory.getLogger(BorsaIstanbulPublishScheduler.class);

    private final BorsaIstanbulCacheService cacheService;
    private final CollectApiClient client;
    private final SimpMessagingTemplate ws;
    private final String topic;
    private final StockProperties.Scheduler.Publish cfg;

    public BorsaIstanbulPublishScheduler(BorsaIstanbulCacheService cacheService, CollectApiClient client,
                                         SimpMessagingTemplate ws,
                                         StockProperties stockProperties) {
        this.cacheService = cacheService;
        this.client = client;
        this.ws     = ws;
        this.cfg    = stockProperties.getScheduler().getBorsaIstanbulPublish();
        this.topic  = cfg.getTopic();
    }

    @Scheduled(
            fixedRateString    = "${stock.scheduler.borsa-istanbul-publish.rate}",
            initialDelayString = "${stock.scheduler.borsa-istanbul-publish.initial-delay}"
    )
    public void run() {


        var list = cacheService.getAll();      // <— ilk çağrıda API, sonraki 5dk cache
        if (!list.isEmpty()) {
            ws.convertAndSend(topic, list.get(0));
        }
    }
}