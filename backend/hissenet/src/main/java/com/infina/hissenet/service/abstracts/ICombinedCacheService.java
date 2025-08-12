package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.response.CombinedStockData;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing the combined stock data cache.
 * <p>
 * This service provides operations to retrieve all cached combined stock entries,
 * fetch a specific entry by its stock code, and refresh the cache asynchronously
 * from upstream data sources.
 * </p>
 *
 * <p>The combined stock data typically merges information from multiple providers
 * (e.g., Collect API and Infina API) into a unified structure.</p>
 *
 * @author Fatma Nur Kurt
 * @version 1.0
 */
public interface ICombinedCacheService {

    /**
     * Retrieves all cached {@link CombinedStockData} entries.
     *
     * @return a list of combined stock data; returns an empty list if the cache is empty
     */
    List<CombinedStockData> getAll();

    /**
     * Retrieves a specific cached {@link CombinedStockData} entry by its stock code.
     *
     * @param code the stock code (case-insensitive)
     * @return an {@link Optional} containing the matching entry, or empty if not found
     */
    Optional<CombinedStockData> getByCode(String code);

    /**
     * Refreshes the combined stock data cache asynchronously by fetching fresh data
     * from upstream sources and replacing the current snapshot.
     *
     * @return a {@link Mono} that completes when the refresh process finishes
     */
    Mono<Void> refreshAsync();
}
