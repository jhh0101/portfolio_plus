package com.portfolio.auctionmarket.domain.ai.client;

import com.portfolio.auctionmarket.global.config.LangfuseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class LangfuseApiClient {
    private final WebClient webClient;

    public LangfuseApiClient(@Qualifier("langfuseWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public void logGeneration(String aiTaskName, String userInput, String aiOutput, Double score) {
        String traceId = UUID.randomUUID().toString();
        String now = OffsetDateTime.now().format(DateTimeFormatter.ISO_INSTANT);

        Map<String, Object> requestBody = Map.of(
                "batch", List.of(
                        Map.of(
                                "id", UUID.randomUUID().toString(),
                                "timestamp", now,
                                "type", "trace-create",
                                "body", Map.of(
                                        "id", traceId,
                                        "name", aiTaskName,
                                        "input", userInput,
                                        "output", aiOutput,
                                        "metadata", Map.of("similarity", score != null ? score : 0.0)
                                )
                        ),
                        Map.of(
                                "id", UUID.randomUUID().toString(),
                                "timestamp", now,
                                "type", "observation-create",
                                "body", Map.of(
                                        "trace", traceId,
                                        "type", "GENERATION",
                                        "name", aiTaskName + "-llm",
                                        "model", "llama-3.3-70b-versatile",
                                        "input", userInput,
                                        "output", aiOutput,
                                        "startTime", now,
                                        "metadata", Map.of("similarity", score != null ? score : 0.0)
                                )
                        )
                )
        );

        webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info("Langfuse로 AI 로그 전송 성공 : {}", response),
                        error -> log.info("Langfuse로 AI 로그 전송 실패 : {}", error.getMessage())
                );
    }
}
