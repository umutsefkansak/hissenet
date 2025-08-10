package com.infina.hissenet.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "fetch")
@Validated
public class FetchProperties {
    @NotNull
    private Duration requestTimeout;

    @Min(1)
    private int concurrency = 16;

    @Min(0)
    private int retry = 2;

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Duration requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }
}
