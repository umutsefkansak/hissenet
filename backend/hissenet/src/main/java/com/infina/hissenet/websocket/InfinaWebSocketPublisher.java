package com.infina.hissenet.websocket;

import com.infina.hissenet.dto.response.HisseFiyatEntry;
import com.infina.hissenet.properties.InfinaSchedulerProperties;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Publishes Infina price update data and error messages to WebSocket topics.
 * <p>
 * This component uses Spring's {@link SimpMessagingTemplate} to send
 * messages to the configured STOMP destinations. The target topics
 * are retrieved from {@link InfinaSchedulerProperties}.
 * </p>
 *
 * <p>Typical usage:</p>
 * <ul>
 *   <li>Send a list of {@link HisseFiyatEntry} updates to subscribers.</li>
 *   <li>Publish error messages to a dedicated error topic.</li>
 * </ul>
 *
 * <p>
 * This is typically used by scheduled tasks or data fetchers to push
 * new Infina price data to connected WebSocket clients.
 * </p>
 *
 * @see SimpMessagingTemplate
 * @see InfinaSchedulerProperties
 */
@Component
public class InfinaWebSocketPublisher {

    private final SimpMessagingTemplate template;
    private final InfinaSchedulerProperties props;

    public InfinaWebSocketPublisher(SimpMessagingTemplate template, InfinaSchedulerProperties props) {
        this.template = template;
        this.props = props;
    }

    /**
     * Publishes a list of stock price entries to the configured data topic.
     *
     * @param data the list of {@link HisseFiyatEntry} to be sent to subscribers;
     *             should not be {@code null}
     */
    public void publish(List<HisseFiyatEntry> data) {
        template.convertAndSend(props.getTopic(), data);
    }

    /**
     * Publishes an error message to the configured error topic.
     *
     * @param errorMessage the error message to send; should not be {@code null} or empty
     */
    public void publishError(String errorMessage) {
        template.convertAndSend(props.getErrorTopic(), errorMessage);
    }
}

