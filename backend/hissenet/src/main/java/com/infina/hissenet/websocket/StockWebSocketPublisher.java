package com.infina.hissenet.websocket;


import com.infina.hissenet.dto.response.StockData;
import com.infina.hissenet.properties.StockProperties;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Publishes {@link StockData} updates to WebSocket subscribers.
 * <p>
 * This component is responsible for sending stock market data to the
 * configured STOMP topic using Spring's {@link SimpMessagingTemplate}.
 * The target topic is determined from {@link StockProperties} configuration.
 * </p>
 *
 * <p>Typical usage:</p>
 * <ul>
 *   <li>Used by schedulers or services to broadcast the latest stock data.</li>
 *   <li>Ensures all connected WebSocket clients receive real-time updates.</li>
 * </ul>
 *
 * @see SimpMessagingTemplate
 * @see StockProperties
 */
@Component
public class StockWebSocketPublisher {

    private final SimpMessagingTemplate template;
    private final StockProperties props;

    public StockWebSocketPublisher(SimpMessagingTemplate template, StockProperties props) {
        this.template = template;
        this.props = props;
    }

    /**
     * Publishes the provided stock data list to the configured WebSocket topic.
     *
     * @param data the list of {@link StockData} to send; should not be {@code null}
     */
    public void publish(List<StockData> data) {
        String topic = props.getScheduler().getPublish().getTopic();
        template.convertAndSend(topic, data);
    }
}