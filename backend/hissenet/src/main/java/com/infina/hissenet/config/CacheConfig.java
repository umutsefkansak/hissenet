package com.infina.hissenet.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.infina.hissenet.properties.StockProperties;
import com.infina.hissenet.service.BorsaIstanbulCacheService;
import com.infina.hissenet.service.CombinedCacheService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public Caffeine<Object,Object> caffeineConfig(StockProperties props) {
        var def = props.getCache().getDefault();
        return Caffeine.newBuilder()
                .expireAfterWrite(def.getTtl())
                .maximumSize(def.getMaxSize());
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object,Object> caffeine) {
        CaffeineCacheManager mgr = new CaffeineCacheManager(
                CombinedCacheService.CACHE_NAME,
                BorsaIstanbulCacheService.CACHE_NAME);
        mgr.setCaffeine(caffeine);
        return mgr;
    }
}