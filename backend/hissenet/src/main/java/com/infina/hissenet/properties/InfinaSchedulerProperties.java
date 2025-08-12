package com.infina.hissenet.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "infina.scheduler.publish")
public class InfinaSchedulerProperties {
    private String topic;
    private String errorTopic;

    private Duration rate;
    private Duration initialDelay;

    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Duration getRate() {
        return rate;
    }
    public void setRate(Duration rate) {
        this.rate = rate;
    }

    public Duration getInitialDelay() {
        return initialDelay;
    }
    public void setInitialDelay(Duration initialDelay) {
        this.initialDelay = initialDelay;
    }

    public String getErrorTopic() {
        return errorTopic;
    }

    public void setErrorTopic(String errorTopic) {
        this.errorTopic = errorTopic;
    }
}