package com.infina.hissenet.websocket;

import com.infina.hissenet.dto.response.HisseFiyatEntry;
import com.infina.hissenet.properties.InfinaSchedulerProperties;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InfinaWebSocketPublisher {
    private final SimpMessagingTemplate template;
    private final InfinaSchedulerProperties props;

    public InfinaWebSocketPublisher(SimpMessagingTemplate template, InfinaSchedulerProperties props) {
        this.template = template;
        this.props = props;
    }

    public void publish(List<HisseFiyatEntry> data) {
        template.convertAndSend(props.getTopic(), data);
    }

    /** Publish error message */
    public void publishError(String errorMessage) {
        template.convertAndSend(props.getErrorTopic(), errorMessage);
    }
}

