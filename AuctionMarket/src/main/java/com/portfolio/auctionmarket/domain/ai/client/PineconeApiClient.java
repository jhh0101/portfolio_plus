package com.portfolio.auctionmarket.domain.ai.client;

import com.portfolio.auctionmarket.domain.ai.dto.SimilarityResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.*;

@Slf4j
@Component
public class PineconeApiClient {
    private final WebClient webClient;

    public PineconeApiClient(@Qualifier("pineconeWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> retrieveContext(List<Double> vector) {
        var requestBody = Map.of(
                "vector", vector,
                "topK", 3,
                "includeMetadata", true
        );

        return webClient.post()
                .uri("/query")
                .bodyValue(requestBody)
                .retrieve()
                // 💡 String 통째로 넘기지 않고 JsonNode로 받아서 파싱합니다!
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    log.info("파인콘 검색 결과 수신 완료");
                    StringBuilder contextBuilder = new StringBuilder();
                    JsonNode matches = json.path("matches");

                    // 💡 배열을 돌면서 "metadata" 안의 "text"만 뽑아내어 깔끔한 문자열로 만듭니다.
                    if (matches.isArray() && !matches.isEmpty()) {
                        for (JsonNode match : matches) {
                            JsonNode metadata = match.path("metadata");
                            if (metadata.has("text")) {
                                String text = metadata.path("text").asText();
                                contextBuilder.append("- ").append(text).append("\n");
                            }
                        }
                    }

                    return contextBuilder.toString();
                })
                .onErrorResume(e -> {
                    log.error("pinecone 검색 중 에러 {}", e.getMessage());
                    return Mono.just("");
                });
    }

    public Mono<Void> upsert(String id, List<Double> values, String content) {
        Map<String, Object> vector = Map.of(
                "id", id,
                "values", values,
                "metadata", Map.of("text", content)
        );

        Map<String, Object> requestBody = Map.of(
                "vectors", List.of(vector)
        );

        return webClient.post()
                .uri("/vectors/upsert")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("🚀 파인콘 지식 저장 완료! ID: {}", id));
    }

    public Mono<SimilarityResult> checkSemanticCache(List<Double> vector) {
        Map<String, Object> requestBody = Map.of(
                "vector", vector,
                "topK", 1,
                "includeMetadata", true,
                "namespace", "semantic-cache"
        );

        return webClient.post()
                .uri("/query")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .mapNotNull(json -> {
                    JsonNode matches = json.path("matches");
                    if (matches.isMissingNode() || matches.isEmpty()) {
                        return null;
                    }
                    JsonNode bestMatch = matches.get(0);
                    Double score = bestMatch.path("score").asDouble();
                    String answer = bestMatch.path("metadata").path("answer").asText("");

                    return new SimilarityResult(answer, score);
                })
                .filter(obj -> true);
    }

    public Mono<Void> upsertCache(String id, List<Double> values, String question, String answer) {
        Map<String, Object> vector = Map.of(
                "id", id,
                "values", values,
                "metadata", Map.of(
                        "question", question,
                        "answer", answer
                )
        );

        Map<String, Object> requestBody = Map.of(
                "vectors", List.of(vector),
                "namespace", "semantic-cache"
        );

        return webClient.post()
                .uri("/vectors/upsert")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Pinecone 시맨틱 캐시 저장 완료"));
    }

    public void deleteAll(String namespace) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("deleteAll", true);

        if (namespace != null && !namespace.isEmpty()) {
            requestBody.put("namespace", namespace);
        }

        webClient.post()
                .uri("/vectors/delete")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("🗑️ 파인콘 [{}] 공간 데이터 싹쓸이 완료!", namespace != null && !namespace.isEmpty() ? namespace : "기본 공간"))
                .doOnError(e -> log.error("파인콘 데이터 삭제 실패: ", e))
                .block();
    }
}