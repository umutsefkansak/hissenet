package com.infina.hissenet.listener;

import com.infina.hissenet.client.StockApiClient;
import com.infina.hissenet.dto.request.StockCreateRequest;
import com.infina.hissenet.dto.response.StockApiResponse;
import com.infina.hissenet.dto.request.StockData;
import com.infina.hissenet.entity.enums.Currency;
import com.infina.hissenet.entity.enums.Exchange;
import com.infina.hissenet.entity.enums.Status;
import com.infina.hissenet.properties.CollectApiProperties;
import com.infina.hissenet.service.StockService;
import jakarta.transaction.Transactional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class StockInitializationListener {

    private final StockApiClient apiClient;
    private final StockService stockService;
    private final CollectApiProperties collectApiProperties;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public StockInitializationListener(StockApiClient apiClient, StockService stockService,
                                       CollectApiProperties collectApiProperties) {
        this.apiClient = apiClient;
        this.stockService = stockService;
        this.collectApiProperties = collectApiProperties;
    }

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }

        Mono<StockApiResponse> mono = apiClient.fetchStocks();
        StockApiResponse resp = mono.block();

        if (resp == null || resp.result() == null) {
            return;
        }

        List<StockData> data = resp.result();

        data.stream()
                .map(this::toCreateRequest)
                .forEach(req -> {
                    if (!stockService.existsByTicker(req.ticker())) {
                        stockService.createStock(req);
                    }
                });
    }

    private StockCreateRequest toCreateRequest(StockData d) {
        return new StockCreateRequest(
                d.code(),                    // ticker
                d.text(),                   // issuerName
                Currency.TRY,
                Exchange.BIST,
                null,
                Status.ACTIVE
        );
    }
}