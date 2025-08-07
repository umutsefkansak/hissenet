package com.infina.hissenet.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "stock")
public class StockProperties {
    private final Cache cache = new Cache();
    private final Scheduler scheduler = new Scheduler();

    public Cache getCache() {
        return cache;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public static class Cache {
        private final DefaultConfig defaultConfig = new DefaultConfig();
        public DefaultConfig getDefault() { return defaultConfig; }

        public static class DefaultConfig {
            private String name;
            private Duration ttl;
            private long maxSize;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }

            public Duration getTtl() { return ttl; }
            public void setTtl(Duration ttl) { this.ttl = ttl; }

            public long getMaxSize() { return maxSize; }
            public void setMaxSize(long maxSize) { this.maxSize = maxSize; }
        }
    }

    public static class Scheduler {
        private final Publish publish = new Publish();
        private Publish borsaIstanbulPublish = new Publish();

        public Publish getPublish() { return publish; }

        public Publish getBorsaIstanbulPublish() {
            return borsaIstanbulPublish;
        }

        public void setBorsaIstanbulPublish(Publish borsaIstanbulPublish) {
            this.borsaIstanbulPublish = borsaIstanbulPublish;
        }

        public static class Publish {
            private String topic;
            private Duration rate;
            private Duration initialDelay;

            public String getTopic() { return topic; }
            public void setTopic(String topic) { this.topic = topic; }

            public Duration getRate() { return rate; }
            public void setRate(Duration rate) { this.rate = rate; }

            public Duration getInitialDelay() { return initialDelay; }
            public void setInitialDelay(Duration initialDelay) { this.initialDelay = initialDelay; }
        }
    }
}
