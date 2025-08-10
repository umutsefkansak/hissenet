package com.infina.hissenet.config;

import com.infina.hissenet.service.BorsaIstanbulCacheService;
import com.infina.hissenet.service.CombinedCacheService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupWarmup {

    @Bean
    public ApplicationRunner warmUp(CombinedCacheService combined, BorsaIstanbulCacheService bist) {
        return args -> {
            try {
                System.out.println("[WarmUp] combined refresh");
                combined.refreshAsync().block();
                System.out.println("[WarmUp] bist refresh");
                bist.refreshAsync().block();
            } catch (Exception e) {
                System.out.println("[WarmUp] hata: " + e.getMessage());
            }
        };
    }
}
