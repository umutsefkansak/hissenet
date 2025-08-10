package com.infina.hissenet.service;

import com.infina.hissenet.service.abstracts.ICacheFacade;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Component
public class CacheFacade implements ICacheFacade {

    private static final String ALL_KEY = "ALL";

    private final CacheManager cacheManager;

    public CacheFacade(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getSnapshot(String cacheName) {
        Cache cache = Objects.requireNonNull(cacheManager.getCache(cacheName), "arama Cache not found: " + cacheName);
        Object value = cache.get(ALL_KEY, Object.class);
        if (value instanceof List<?>) {
            return (List<T>) value;
        }
        return List.of();
    }

    @Override
    public <T> void putIfNonEmpty(String cacheName, List<T> newSnapshot) {
        if (newSnapshot == null || newSnapshot.isEmpty()) return;
        Cache cache = getRequiredCache(cacheName);
        cache.put(ALL_KEY, newSnapshot);
    }

    @Override
    public <T> Optional<T> findByCode(String cacheName, String code, Function<T, String> codeExtractor) {
        if (code == null || code.isBlank()) return Optional.empty();
        List<T> snapshot = this.<T>getSnapshot(cacheName);
        return snapshot.stream()
                .filter(it -> {
                    if (it == null) return false;
                    String extracted = codeExtractor.apply(it);
                    return extracted != null && extracted.trim().equalsIgnoreCase(code.trim());
                })
                .findFirst();
    }

    private Cache getRequiredCache(String cacheName) {
        return Objects.requireNonNull(
                cacheManager.getCache(cacheName),
                () -> "Cache not found: " + cacheName
        );
    }
}