package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.StockData;
import com.infina.hissenet.service.abstracts.IStockCacheService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class StockCacheService implements IStockCacheService {

    private final CacheManager cacheManager;
    private static final String CACHE_NAME = "stockPrice";
    private static final String CACHE_KEY = "ALL";

    public StockCacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings("unchecked")
    public List<StockData> getCachedStocks() {
        Cache cache = cacheManager.getCache("stockPrice");
        if (cache == null) {
            return Collections.emptyList();
        }
        List<StockData> stocks = cache.get(MarketDataService.ALL_KEY, List.class);
        return stocks != null ? stocks : Collections.emptyList();
    }


    @Override
    public BigDecimal getPriceByCodeOrNull(String code) {
        return getCachedStocks()
                .stream()
                .filter(s -> s.code().equalsIgnoreCase(code))
                .map(StockData::lastprice)
                .findFirst()
                .orElse(null);
    }

}