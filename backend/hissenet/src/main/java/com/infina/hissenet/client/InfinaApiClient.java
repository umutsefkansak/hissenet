package com.infina.hissenet.client;

import com.infina.hissenet.dto.response.HisseApiResponse;
import com.infina.hissenet.exception.stock.InfinaApiRateLimitException;
import com.infina.hissenet.exception.stock.InfinaApiUpstreamException;
import com.infina.hissenet.properties.InfinaApiProperties;
import com.infina.hissenet.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class InfinaApiClient {
    private final WebClient infinaApiWebClient;
    private final InfinaApiProperties props;

    public InfinaApiClient(WebClient infinaApiWebClient, InfinaApiProperties props) {
        this.infinaApiWebClient = infinaApiWebClient;
        this.props = props;
    }

    public Mono<HisseApiResponse> fetchPriceByCodeAndDate(String assetCode, String date) {
        return infinaApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(props.getEndpoint())
                        .queryParam("api_key", props.getApiKey())
                        .queryParam("asset_code", assetCode)
                        .queryParam("data_date", date)
                        .build())
                .retrieve()
                .bodyToMono(HisseApiResponse.class)
                .defaultIfEmpty(new HisseApiResponse(null))
                .onErrorResume(ex -> {
                    if (ex instanceof WebClientResponseException wex) {
                        int sc = wex.getRawStatusCode();
                        if (sc == 429) return Mono.error(new InfinaApiRateLimitException(MessageUtils.getMessage("collect.api.rate.limit")));
                        else if (sc >= 500) return Mono.error(new InfinaApiUpstreamException(MessageUtils.getMessage("collect.api.upstream.stocks")));
                    }
                    return Mono.just(new HisseApiResponse(null));
                });
    }

}
