package com.infina.hissenet.config;

import com.infina.hissenet.properties.InfinaApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class InfinaApiWebClientConfig {
    @Bean
    public WebClient infinaApiWebClient(WebClient.Builder builder,
                                     InfinaApiProperties props) {
        return builder
                .baseUrl(props.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
                .build();
    }
}
