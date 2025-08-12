package com.infina.hissenet.service;

import com.infina.hissenet.dto.response.BorsaIstanbulResult;
import com.infina.hissenet.service.abstracts.IBorsaIstanbulCacheService;
import com.infina.hissenet.service.abstracts.ICacheFacade;
import com.infina.hissenet.service.abstracts.ICacheRefreshService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class BorsaIstanbulCacheService implements IBorsaIstanbulCacheService {
    public static final String CACHE_NAME = "bist100";

    private final ICacheFacade cache;
    private final ICacheRefreshService<BorsaIstanbulResult> refresher;

    public BorsaIstanbulCacheService(ICacheFacade cache, ICacheRefreshService<BorsaIstanbulResult> refresher) {
        this.cache = cache;
        this.refresher = refresher;
    }

    @Override
    public List<BorsaIstanbulResult> getAll() {
        return cache.getSnapshot(CACHE_NAME);
    }

    @Override
    public Optional<BorsaIstanbulResult> getByCode(String code) {
        return cache.findByCode(CACHE_NAME, code, r -> String.valueOf(r.current()));
    }

    @Override
    public Mono<Void> refreshAsync() {
        return refresher.buildSnapshot()
                .doOnNext(list -> cache.putIfNonEmpty(CACHE_NAME, list))
                .then();
    }
}