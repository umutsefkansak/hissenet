package com.infina.hissenet.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.infina.hissenet.properties.StockProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig(StockProperties props) {
        StockProperties.Cache.DefaultConfig cfg = props.getCache().getDefault();
        return Caffeine.newBuilder()
                .expireAfterWrite(cfg.getTtl())
                .maximumSize(cfg.getMaxSize())
                .recordStats();
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine,
                                     StockProperties props) {
        String cacheName = props.getCache().getDefault().getName();
        CaffeineCacheManager manager = new CaffeineCacheManager(cacheName);
        manager.setCaffeine(caffeine);
        return manager;
    }
}