package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.response.BorsaIstanbulResult;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface IBorsaIstanbulCacheService {

    List<BorsaIstanbulResult> getAll();
    Optional<BorsaIstanbulResult> getByCode(String code);
    Mono<Void> refreshAsync();
}
