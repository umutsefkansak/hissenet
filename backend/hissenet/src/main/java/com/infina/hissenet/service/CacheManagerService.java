package com.infina.hissenet.service;

import com.infina.hissenet.dto.response.CombinedStockData;
import com.infina.hissenet.service.abstracts.ICacheManagerService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CacheManagerService implements ICacheManagerService {

    private final CacheManager cacheManager;
    private static final String CACHE_KEY = "ALL";

    public CacheManagerService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    /**
     * Cache'den sadece belirtilen koda ait CombinedStockData'yı döner.
     * Eğer cache’de yoksa null döner.
     */
    public CombinedStockData getCachedByCode(String code) {
        Cache cache = cacheManager.getCache(CombinedCacheService.CACHE_NAME);
        List<CombinedStockData> all =
                cache.get(CACHE_KEY, List.class);

        return all != null ? all.stream()
                .filter(c -> c.code().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null) : null;
    }

    /**
     * Cache'deki tüm entry’leri map olarak döner: key=code, value=CombinedStockData
     */
    @SuppressWarnings("unchecked")
    public List<CombinedStockData> getAllCached() {
        Cache cache = cacheManager.getCache(CombinedCacheService.CACHE_NAME);
        if (cache == null) return List.of();
        List<CombinedStockData> all =
                cache.get(CACHE_KEY, List.class);
        return all != null ? all : List.of();
    }
}
