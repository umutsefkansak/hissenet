package com.infina.hissenet.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "collectapi")
@Validated
public class CollectApiProperties {

    @NotBlank
    private String apiKey;
    @NotBlank
    private String baseUrl;

    private Duration cooldown = Duration.ofSeconds(30);

    private Endpoints endpoints = new Endpoints();

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Duration getCooldown() {
        return cooldown;
    }

    public void setCooldown(Duration cooldown) {
        this.cooldown = cooldown;
    }

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public static class Endpoints {
        private String stocks;
        private String borsaIstanbul;

        public String getStocks() {
            return stocks;
        }

        public void setStocks(String stocks) {
            this.stocks = stocks;
        }

        public String getBorsaIstanbul() {
            return borsaIstanbul;
        }

        public void setBorsaIstanbul(String borsaIstanbul) {
            this.borsaIstanbul = borsaIstanbul;
        }
    }
}