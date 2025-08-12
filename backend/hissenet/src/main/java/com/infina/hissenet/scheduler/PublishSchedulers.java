package com.infina.hissenet.scheduler;

import com.infina.hissenet.properties.StockProperties;
import com.infina.hissenet.service.BorsaIstanbulCacheService;
import com.infina.hissenet.service.CombinedCacheService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PublishSchedulers {

    private final CombinedCacheService combinedCache;
    private final BorsaIstanbulCacheService bistCache;
    private final SimpMessagingTemplate ws;

    private final String combinedTopic;
    private final String bistTopic;

    public PublishSchedulers(CombinedCacheService combinedCache,
                             BorsaIstanbulCacheService bistCache,
                             SimpMessagingTemplate ws,
                             StockProperties stockProperties) {
        this.combinedCache = combinedCache;
        this.bistCache = bistCache;
        this.ws = ws;

        this.combinedTopic = stockProperties.getScheduler().getPublish().getTopic();
        this.bistTopic     = stockProperties.getScheduler().getBorsaIstanbulPublish().getTopic();
    }

    @Scheduled(fixedRateString = "${stock.scheduler.publish.rate}",
            initialDelayString = "${stock.scheduler.publish.initial-delay}")
    public void publishCombined() {
        var snapshot = combinedCache.getAll();
        if (snapshot.isEmpty()) {
            System.out.println("[Publish] /topic/prices BOS → gönderim atlandı");
            return;
        }
        snapshot.stream().limit(5).forEach(s ->
                System.out.println("  * " + s.code() + " last=" + s.lastPrice() + " rate=" + s.rate()));
        ws.convertAndSend(combinedTopic, snapshot);
    }


    @Scheduled(fixedRateString = "${stock.scheduler.borsa-istanbul-publish.rate}",
            initialDelayString = "${stock.scheduler.borsa-istanbul-publish.initial-delay}")
    public void publishBist() {
        var list = bistCache.getAll();
        if (!list.isEmpty()) {
            ws.convertAndSend(bistTopic, list.get(0));
        }
    }
}
