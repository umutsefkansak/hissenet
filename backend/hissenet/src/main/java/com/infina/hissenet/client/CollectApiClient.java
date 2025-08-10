package com.infina.hissenet.client;


import com.infina.hissenet.dto.response.BorsaIstanbulApiResponse;
import com.infina.hissenet.dto.response.StockApiResponse;
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

@Component
public class CollectApiClient {
    private final WebClient collectApiWebClient;
    private final CollectApiProperties props;
    private final AtomicLong coolDownUntilEpochMs = new AtomicLong(0);


    public CollectApiClient(WebClient collectApiWebClient, CollectApiProperties props) {
        this.collectApiWebClient = collectApiWebClient;
        this.props = props;
    }

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
                        return Mono.just(new StockApiResponse(false, Collections.emptyList()));
                    }

                    if (st.is2xxSuccessful()) {
                        return resp.bodyToMono(StockApiResponse.class)
                                .defaultIfEmpty(new StockApiResponse(false, Collections.emptyList()))
                                .map(body -> {
                                    int n = (body.result() != null) ? body.result().size() : 0;
                                    System.out.println("[CollectApiClient] fetchStocks → adet=" + n);
                                    if (n > 0) {
                                        body.result().stream().limit(5).forEach(s ->
                                                System.out.println("  - " + s.code() + " last=" + s.lastprice() + " rate=" + s.rate()));
                                    }
                                    return body;
                                });
                    }

                    return resp.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .doOnNext(b -> System.out.println("[CollectApiClient] HTTP hata: " + st + " body=" + b))
                            .thenReturn(new StockApiResponse(false, Collections.emptyList()));
                })
                .onErrorResume(e -> {
                    System.out.println("[CollectApiClient] HATA fetchStocks: " + e.getMessage());
                    return Mono.just(new StockApiResponse(false, Collections.emptyList()));
                });
    }

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
                            .doOnNext(b -> System.out.println("[CollectApiClient] fetchBorsaIstanbul HTTP hata: " + st + " body=" + b))
                            .thenReturn(new BorsaIstanbulApiResponse(false, Collections.emptyList()));
                })
                .onErrorResume(e -> {
                    System.out.println("[CollectApiClient] HATA fetchBorsaIstanbul: " + e.getMessage());
                    return Mono.just(new BorsaIstanbulApiResponse(false, Collections.emptyList()));
                });
    }
}