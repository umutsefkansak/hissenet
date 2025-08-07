package com.infina.hissenet.client;


import com.infina.hissenet.dto.response.BorsaIstanbulApiResponse;
import com.infina.hissenet.dto.response.StockApiResponse;
import com.infina.hissenet.properties.CollectApiProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class CollectApiClient {
    private final WebClient collectApiWebClient;
    private final CollectApiProperties props;

    public CollectApiClient(WebClient collectApiWebClient, CollectApiProperties props) {
        this.collectApiWebClient = collectApiWebClient;
        this.props = props;
    }

    public Mono<StockApiResponse> fetchStocks() {
        String uri = props.getEndpoints().getStocks();
        System.out.println("uri" + uri);
        return collectApiWebClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "apikey " + props.getApiKey())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.CONNECTION,     "close")
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(StockApiResponse.class);
                    } else {
                        System.err.println("API hata kodu: " + response.statusCode());
                        return Mono.just(new StockApiResponse(false, Collections.emptyList()));
                    }
                })
                .doOnError(e -> System.err.println("fetchStocks hatası: " + e.getMessage()));
    }

    public Mono<BorsaIstanbulApiResponse> fetchBorsaIstanbul() {
        String uri = props.getEndpoints().getBorsaIstanbul();
        return collectApiWebClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "apikey " + props.getApiKey())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchangeToMono(resp -> {
                    if (resp.statusCode().is2xxSuccessful()) {
                        return resp.bodyToMono(BorsaIstanbulApiResponse.class);
                    } else {
                        // hata durumunda success=false, result null
                        return Mono.just(new BorsaIstanbulApiResponse(false, null));
                    }
                })
                .doOnError(e -> System.err.println("fetchBorsaIstanbul hatası: " + e.getMessage()));
    }
}
