package com.infina.hissenet.service;

import com.infina.hissenet.dto.response.CombinedStockData;
import com.infina.hissenet.service.abstracts.ICacheManagerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CacheManagerService implements ICacheManagerService {

    private final CombinedCacheService combined;

    public CacheManagerService(CombinedCacheService combined) {
        this.combined = combined;
    }

    public List<CombinedStockData> getAllCached() {
        return combined.getAll();
    }

    public CombinedStockData getCachedByCode(String code) {
        Optional<CombinedStockData> opt = combined.getByCode(code);
        return opt.orElse(null);
    }
}
