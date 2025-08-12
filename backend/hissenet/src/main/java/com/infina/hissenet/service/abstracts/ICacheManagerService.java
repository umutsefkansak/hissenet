package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.response.CombinedStockData;

import java.util.List;

/**
 * Service interface for managing combined stock data cache operations.
 * <p>
 * Provides methods to retrieve all cached combined stock data or a single record
 * by its stock code. This service acts as a higher-level manager that can
 * aggregate data from multiple cache sources.
 * </p>
 * <p>
 * Typical usage is to serve cached data quickly to API endpoints without
 * querying external data sources each time.
 * </p>
 *
 * @author Fatma Nur Kurt
 * @version 1.0
 */
public interface ICacheManagerService {

    /**
     * Retrieves all cached {@link CombinedStockData} entries.
     *
     * @return a list of combined stock data; returns an empty list if no data is cached
     */
    List<CombinedStockData> getAllCached();

    /**
     * Retrieves a single cached {@link CombinedStockData} entry by stock code.
     *
     * @param code the stock code (case-insensitive)
     * @return the matching {@link CombinedStockData}, or {@code null} if not found in cache
     */
    CombinedStockData getCachedByCode(String code);
}
