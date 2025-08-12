package com.infina.hissenet.service;

import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.service.abstracts.ICacheFacade;
import com.infina.hissenet.service.abstracts.ICacheRefreshService;
import com.infina.hissenet.service.abstracts.ICombinedCacheService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CombinedCacheService implements ICombinedCacheService {

    public static final String CACHE_NAME = "combinedStock";

    private final ICacheFacade cache;
    private final ICacheRefreshService<CombinedStockData> refresher;

    private final AtomicBoolean refreshing = new AtomicBoolean(false);

    public CombinedCacheService(ICacheFacade cache, ICacheRefreshService<CombinedStockData> refresher) {
        this.cache = cache;
        this.refresher = refresher;
    }

    @Override
    public List<CombinedStockData> getAll() {
        return cache.getSnapshot(CACHE_NAME);
    }

    @Override
    public Optional<CombinedStockData> getByCode(String code) {
        return cache.findByCode(CACHE_NAME, code, CombinedStockData::code);
    }

    @Override
    public Mono<Void> refreshAsync() {
        if (!refreshing.compareAndSet(false, true)) {
            return Mono.empty();
        }
        return refresher.buildSnapshot()
                .doOnNext(list -> {
                    if (!list.isEmpty()) {
                        cache.putIfNonEmpty(CACHE_NAME, list);                    } else {
                    }
                })
                .doFinally(sig -> refreshing.set(false))
                .then();
    }

}
