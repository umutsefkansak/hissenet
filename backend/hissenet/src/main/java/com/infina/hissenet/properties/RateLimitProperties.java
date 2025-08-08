package com.infina.hissenet.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {
    private int capacity = 100;
    private int timeInMinutes = 1;

    public int getTimeInMinutes() {
        return timeInMinutes;
    }

    public int getCapacity() {
        return capacity;
    }
}
