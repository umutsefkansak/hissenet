package com.infina.hissenet.client;


import com.infina.hissenet.dto.response.BorsaIstanbulApiResponse;
import com.infina.hissenet.dto.response.StockApiResponse;
import com.infina.hissenet.exception.stock.CollectApiRateLimitException;
import com.infina.hissenet.exception.stock.CollectApiUpstreamException;
import com.infina.hissenet.properties.CollectApiProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Client for interacting with the Collect API service.
 * <p>
 * Provides methods to retrieve:
 * <ul>
 *   <li>General stock data (fetchStocks)</li>
 *   <li>Borsa Istanbul data (fetchBorsaIstanbul)</li>
 * </ul>
 * Handles:
 * <ul>
 *   <li>API key authentication via {@code Authorization} header</li>
 *   <li>Rate limiting via cooldown logic</li>
 *   <li>Graceful fallback with empty responses when errors occur</li>
 * </ul>
 *
 * This client uses {@link WebClient} for non-blocking HTTP calls and
 * integrates with exception types for upstream and rate-limit errors.
 *
 * @see WebClient
 * @see CollectApiProperties
 * @see CollectApiRateLimitException
 * @see CollectApiUpstreamException
 */
@Component
public class CollectApiClient {
    private final WebClient collectApiWebClient;
    private final CollectApiProperties props;
    private final AtomicLong coolDownUntilEpochMs = new AtomicLong(0);


    public CollectApiClient(WebClient collectApiWebClient, CollectApiProperties props) {
        this.collectApiWebClient = collectApiWebClient;
        this.props = props;
    }

    /**
     * Fetches stock data from the Collect API's stocks endpoint.
     * <p>
     * - Applies cooldown when a 429 (rate limit) response is received.
     * - Returns an empty {@link StockApiResponse} on errors or cooldown.
     * </p>
     *
     * @return a {@link Mono} emitting the {@link StockApiResponse} or empty data on failure
     * @throws CollectApiRateLimitException if rate limit (429) is triggered
     * @throws CollectApiUpstreamException  if an upstream error occurs
     */
    public Mono<StockApiResponse> fetchStocks() {
        long now = System.currentTimeMillis();
        long until = coolDownUntilEpochMs.get();
        if (now < until) {
            return Mono.just(new StockApiResponse(false, Collections.emptyList()));
        }

        return collectApiWebClient.get()
                .uri(props.getEndpoints().getStocks())
                .header(HttpHeaders.AUTHORIZATION, "apikey " + props.getApiKey())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchangeToMono(resp -> {
                    HttpStatusCode st = resp.statusCode();

                    if (st.value() == 429) {
                        var cd = props.getCooldown() != null ? props.getCooldown() : Duration.ofSeconds(30);
                        coolDownUntilEpochMs.set(System.currentTimeMillis() + cd.toMillis());
                        return Mono.error(new CollectApiRateLimitException("API rate limit aşıldı"));
                    }

                    if (st.is2xxSuccessful()) {
                        return resp.bodyToMono(StockApiResponse.class)
                                .defaultIfEmpty(new StockApiResponse(false, Collections.emptyList()));
                    }

                    return resp.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(b -> Mono.error(new CollectApiUpstreamException("Stocks upstream error: HTTP " + st.value())));
                })
                .onErrorResume(e -> Mono.just(new StockApiResponse(false, Collections.emptyList())));
    }

    /**
     * Fetches Borsa Istanbul data from the Collect API's Borsa Istanbul endpoint.
     * <p>
     * - Returns an empty {@link BorsaIstanbulApiResponse} on errors.
     * - Uses the API key for authentication.
     * </p>
     *
     * @return a {@link Mono} emitting the {@link BorsaIstanbulApiResponse} or empty data on failure
     * @throws CollectApiUpstreamException if an upstream error occurs
     */
    public Mono<BorsaIstanbulApiResponse> fetchBorsaIstanbul() {
        return collectApiWebClient.get()
                .uri(props.getEndpoints().getBorsaIstanbul())
                .header(HttpHeaders.AUTHORIZATION, "apikey " + props.getApiKey())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchangeToMono(resp -> {
                    HttpStatusCode st = resp.statusCode();

                    if (st.is2xxSuccessful()) {
                        return resp.bodyToMono(BorsaIstanbulApiResponse.class)
                                .defaultIfEmpty(new BorsaIstanbulApiResponse(false, Collections.emptyList()));
                    }

                    return resp.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(b -> Mono.error(new CollectApiUpstreamException("BIST upstream error: HTTP " + st.value())));
                })
                .onErrorResume(e -> Mono.just(new BorsaIstanbulApiResponse(false, Collections.emptyList())));

    }
}