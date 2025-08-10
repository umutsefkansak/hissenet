package com.infina.hissenet.client;

import com.infina.hissenet.dto.response.HisseApiResponse;
import com.infina.hissenet.properties.InfinaApiProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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
                .doOnNext(resp -> {
                    int n = 0;
                    if (resp != null && resp.result() != null && resp.result().data() != null && resp.result().data().HisseFiyat() != null)
                        n = resp.result().data().HisseFiyat().size();
                    //System.out.println("[InfinaApiClient] " + assetCode + " @" + date + " → kayıt=" + n);
                })
                .onErrorResume(err -> {
                    //System.out.println("[InfinaApiClient] HATA " + assetCode + " @" + date + ": " + err.getMessage());
                    return Mono.just(new HisseApiResponse(null));
                });
    }

}
