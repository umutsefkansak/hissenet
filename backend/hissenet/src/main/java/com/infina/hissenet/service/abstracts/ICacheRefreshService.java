package com.infina.hissenet.service.abstracts;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Service interface for building and refreshing cache snapshots.
 * <p>
 * Defines a contract for asynchronously constructing a complete cache dataset
 * (snapshot) from one or more data sources. Intended to be used by cache services
 * to periodically refresh stored data without blocking application threads.
 * </p>
 *
 * <p>Generic type {@code <T>} represents the type of objects stored in the cache.</p>
 *
 * @param <T> the type of data objects contained in the cache snapshot
 * @author Fatma Nur Kurt
 * @version 1.0
 */
public interface ICacheRefreshService<T> {

    /**
     * Builds a complete snapshot of data for caching purposes.
     * <p>
     * This method typically fetches data from one or more upstream sources,
     * merges or transforms them if necessary, and returns the resulting list
     * ready to be stored in the cache.
     * </p>
     *
     * @return a {@link Mono} emitting the list of data objects representing the new snapshot
     */
    Mono<List<T>> buildSnapshot();
}
