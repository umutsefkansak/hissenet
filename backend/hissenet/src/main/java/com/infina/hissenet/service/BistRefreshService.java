package com.infina.hissenet.service;

import com.infina.hissenet.client.CollectApiClient;
import com.infina.hissenet.dto.response.BorsaIstanbulResult;
import com.infina.hissenet.service.abstracts.ICacheRefreshService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class BistRefreshService implements ICacheRefreshService<BorsaIstanbulResult> {

    private final CollectApiClient collect;

    public BistRefreshService(CollectApiClient collect) {
        this.collect = collect;
    }

    @Override
    public Mono<List<BorsaIstanbulResult>> buildSnapshot() {
        return collect.fetchBorsaIstanbul()
                .map(resp -> resp != null && resp.result() != null ? resp.result() : Collections.<BorsaIstanbulResult>emptyList())
                .doOnNext(list -> System.out.println("[BistRefresh] adet=" + list.size()))
                .onErrorReturn(Collections.emptyList());
    }
}
