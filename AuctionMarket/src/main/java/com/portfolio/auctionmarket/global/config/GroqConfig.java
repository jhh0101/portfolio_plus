package com.portfolio.auctionmarket.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GroqConfig {
    @Value("${groq.url}")
    private String groqUrl;

    @Value("${groq.api-key}")
    private String groqApiKey;

    @Bean(name = "groqWebClient")
    public WebClient grokWebClient() {
        return WebClient.builder()
                .baseUrl(groqUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
