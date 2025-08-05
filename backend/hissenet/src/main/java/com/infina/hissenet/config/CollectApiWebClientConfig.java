package com.infina.hissenet.config;

import com.infina.hissenet.properties.CollectApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CollectApiWebClientConfig {

    @Bean
    public WebClient collectApiWebClient(WebClient.Builder builder, CollectApiProperties props) {
        String baseUrl = props.getBaseUrl();
        System.out.println("baseurl" + baseUrl);
        return builder
                .baseUrl(baseUrl)
                .build();
    }
}