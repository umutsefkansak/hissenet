package com.infina.hissenet.config;

import com.infina.hissenet.properties.RateLimitProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    private final RateLimitProperties properties;

    public RateLimitConfig(RateLimitProperties properties) {
        this.properties = properties;
    }

    public Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(
                properties.getCapacity(),
                Refill.greedy(properties.getCapacity(), Duration.ofMinutes(properties.getTimeInMinutes()))
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
