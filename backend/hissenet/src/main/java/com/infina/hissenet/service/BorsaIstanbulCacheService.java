package com.infina.hissenet.service;

import com.infina.hissenet.client.CollectApiClient;
import com.infina.hissenet.dto.response.BorsaIstanbulApiResponse;
import com.infina.hissenet.dto.response.BorsaIstanbulResult;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class BorsaIstanbulCacheService {
    public static final String CACHE_NAME = "bist100";
    private static final String CACHE_KEY  = "'ALL'";

    private final CollectApiClient client;

    public BorsaIstanbulCacheService(CollectApiClient client) {
        this.client = client;
    }

    /**
     * İlk çağrıda:
     *  1) API’den listeyi çekip bloklar,
     *  2) sonucu cache’e yazar.
     * Sonraki 5dk içinde (TTL’e göre) direkt cache’den döner.
     */
    @Cacheable(cacheNames = CACHE_NAME, key = CACHE_KEY)
    public List<BorsaIstanbulResult> getAll() {
        BorsaIstanbulApiResponse resp = client.fetchBorsaIstanbul()
                .onErrorReturn(new BorsaIstanbulApiResponse(false, Collections.emptyList()))
                .block();

        return resp != null && resp.result() != null
                ? resp.result()
                : Collections.emptyList();
    }
}