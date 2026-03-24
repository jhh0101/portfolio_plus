package com.portfolio.auctionmarket.global.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Data
@Component
@ConfigurationProperties(prefix = "langfuse")
public class LangfuseConfig {
    private String publicKey;
    private String secretKey;
    private String url;

    @Bean(name = "langfuseWebClient")
    public WebClient grokWebClient() {

        String endpoint = url + "/api/public/ingestion";
        String authStr = publicKey + ":" + secretKey;
        String base64 = Base64.getEncoder().encodeToString(authStr.getBytes());

        return WebClient.builder()
                .baseUrl(endpoint)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + base64)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
