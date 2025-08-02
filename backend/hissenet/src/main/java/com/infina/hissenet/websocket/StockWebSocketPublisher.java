package com.infina.hissenet.websocket;

import com.infina.hissenet.dto.request.StockData;
import com.infina.hissenet.properties.StockProperties;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockWebSocketPublisher {
    private final SimpMessagingTemplate template;
    private final StockProperties props;


    public StockWebSocketPublisher(SimpMessagingTemplate template, StockProperties props) {
        this.template = template;
        this.props = props;
    }

    public void publish(List<StockData> data) {
        String topic = props.getScheduler().getPublish().getTopic();
        template.convertAndSend(topic, data);
    }
}
