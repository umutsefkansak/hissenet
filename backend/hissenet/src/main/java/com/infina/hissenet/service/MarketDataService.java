package com.infina.hissenet.service;

import com.infina.hissenet.client.StockApiClient;
import com.infina.hissenet.dto.request.StockData;
import com.infina.hissenet.dto.response.StockApiResponse;
import com.infina.hissenet.service.abstracts.IMarketDataService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MarketDataService implements IMarketDataService {
    public static final String ALL_KEY = "ALL";
    public static final String CACHE_NAME = "stockPrice";
    private final StockApiClient client;

    public MarketDataService(StockApiClient client) {
        this.client = client;
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME , key = "#root.target.ALL_KEY")
    public List<StockData> getAllStocks() {
        StockApiResponse resp = client.fetchStocks()
                .onErrorReturn(new StockApiResponse(false, Collections.emptyList()))
                .block();

        List<StockData> stocks = Optional.ofNullable(resp)
                .map(StockApiResponse::result)
                .orElseGet(Collections::emptyList);

        return stocks;
    }
}