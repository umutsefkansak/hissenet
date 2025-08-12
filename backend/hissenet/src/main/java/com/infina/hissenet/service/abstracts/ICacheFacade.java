package com.infina.hissenet.service.abstracts;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Facade interface for managing application-level cache operations.
 * <p>
 * Provides generic methods for retrieving, storing, and searching cached data
 * without exposing the underlying cache implementation (e.g., Caffeine, Redis).
 * </p>
 * <p>
 * Typical usage involves working with named caches where a snapshot of the data
 * is stored and can be retrieved as a complete list or by a specific code.
 * </p>
 *
 * @author Fatma Nur Kurt
 * @version 1.0
 */
public interface ICacheFacade {

    /**
     * Retrieves a full snapshot of cached data for the given cache name.
     *
     * @param cacheName the name of the cache
     * @param <T>       the type of objects stored in the cache
     * @return a list containing all cached entries; returns an empty list if the cache is empty
     */
    <T> List<T> getSnapshot(String cacheName);

    /**
     * Puts the given list into the specified cache if the list is non-empty.
     * <p>
     * Intended for refreshing the cache with a complete dataset only when there
     * is valid data to store.
     * </p>
     *
     * @param cacheName the name of the cache
     * @param data      the list of data to cache
     * @param <T>       the type of objects stored in the cache
     */
    <T> void putIfNonEmpty(String cacheName, List<T> data);

    /**
     * Searches for a cached item by its identifying code.
     * <p>
     * The {@code codeExtractor} function is used to extract the identifying
     * code from each cached object.
     * </p>
     *
     * @param cacheName     the name of the cache
     * @param code          the code to search for (case-insensitive)
     * @param codeExtractor a function to extract the identifying code from the cached object
     * @param <T>           the type of objects stored in the cache
     * @return an {@link Optional} containing the found object, or empty if not found
     */
    <T> Optional<T> findByCode(String cacheName, String code, Function<T, String> codeExtractor);
}
