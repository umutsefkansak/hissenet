package com.infina.hissenet.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "collectapi")
public class CollectApiProperties {
    private String apiKey;
    private String baseUrl;
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

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public static class Endpoints {
        private String stocks;
        private String borsaIstanbul;    // ← yeni

        public String getBorsaIstanbul() {
            return borsaIstanbul;
        }

        public void setBorsaIstanbul(String borsaIstanbul) {
            this.borsaIstanbul = borsaIstanbul;
        }

        public String getStocks() {
            return stocks;
        }

        public void setStocks(String stocks) {
            this.stocks = stocks;
        }
    }
}
