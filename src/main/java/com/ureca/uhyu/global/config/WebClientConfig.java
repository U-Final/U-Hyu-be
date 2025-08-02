package com.ureca.uhyu.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${fastapi.url}")
    private String fastApiUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(fastApiUrl)
                .build();
    }
}
