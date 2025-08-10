package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.response.CombinedStockData;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface ICombinedCacheService {
    List<CombinedStockData> getAll();
    Optional<CombinedStockData> getByCode(String code);
    Mono<Void> refreshAsync();
}
