package com.infina.hissenet.config;

import com.infina.hissenet.properties.InfinaApiProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class InfinaApiWebClientConfig {

    @Bean
    public WebClient infinaApiWebClient(WebClient.Builder builder, InfinaApiProperties props) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(8, TimeUnit.SECONDS)))
                .responseTimeout(Duration.ofSeconds(8));

        return builder
                .baseUrl(props.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
