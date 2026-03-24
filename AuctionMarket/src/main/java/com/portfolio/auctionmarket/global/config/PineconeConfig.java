package com.portfolio.auctionmarket.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PineconeConfig {
    @Value("${pinecone.api-key}")
    private String pineconeApiKey;

    @Value("${pinecone.url}")
    private String pineconeApiUrl;

    @Bean(name = "pineconeWebClient")
    public WebClient pineconeWebClient() {
        return WebClient.builder()
                .baseUrl(pineconeApiUrl)
                .defaultHeader("Api-Key", pineconeApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
