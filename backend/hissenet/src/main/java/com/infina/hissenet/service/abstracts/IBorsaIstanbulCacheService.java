package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.response.BorsaIstanbulResult;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


/**
 * Service interface for managing Borsa Istanbul (BIST 100) cache operations.
 * <p>
 * Provides methods to retrieve cached stock market data and refresh the cache
 * from upstream data sources.
 * </p>
 * <p>
 * This service is intended to reduce external API calls and improve data access
 * speed by keeping a local in-memory snapshot of BIST 100 results.
 * </p>
 *
 * @author Fatma Nur Kurt
 * @version 1.0
 */
public interface IBorsaIstanbulCacheService {

    /**
     * Retrieves all cached Borsa Istanbul (BIST 100) stock data.
     *
     * @return a list of {@link BorsaIstanbulResult} representing the cached data;
     * if the cache is empty, an empty list is returned
     */
    List<BorsaIstanbulResult> getAll();

    /**
     * Retrieves a single cached Borsa Istanbul (BIST 100) stock entry by its code.
     *
     * @param code the stock code (case-insensitive)
     * @return an {@link Optional} containing the matching {@link BorsaIstanbulResult}
     * if found, or an empty Optional if not present in cache
     */
    Optional<BorsaIstanbulResult> getByCode(String code);

    /**
     * Asynchronously refreshes the BIST 100 cache by fetching fresh data
     * from the upstream source and updating the cache.
     *
     * @return a {@link Mono} that completes when the refresh process finishes
     */
    Mono<Void> refreshAsync();
}
