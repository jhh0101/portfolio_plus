package com.portfolio.auctionmarket.domain.ai.service;

import com.portfolio.auctionmarket.domain.ai.client.GroqApiClient;
import com.portfolio.auctionmarket.domain.ai.client.LangfuseApiClient;
import com.portfolio.auctionmarket.domain.ai.client.LocalEmbeddingClient;
import com.portfolio.auctionmarket.domain.ai.client.PineconeApiClient;
import com.portfolio.auctionmarket.domain.ai.dto.AiResponse;
import com.portfolio.auctionmarket.domain.ai.dto.SimilarityResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {
    private final LocalEmbeddingClient embeddingClient;
    private final PineconeApiClient pineconeApiClient;
    private final GroqApiClient groqApiClient;
    private final LangfuseApiClient langfuseApiClient;
    private final ChatHistoryService chatHistoryService;

    public Flux<AiResponse> askAuctionBot(String sessionId, String userQuery) {
        log.info("AI 상담 시작 - 세션: {}, 질문: {}", sessionId, userQuery);

        return Mono.fromCallable(() -> {
                    // 1. 사용자 질문을 먼저 히스토리에 저장
                    chatHistoryService.saveMessage(sessionId, "user", userQuery);
                    // 2. 지금까지의 대화 기록을 불러옴 (Groq에 전달용)
                    return chatHistoryService.getHistory(sessionId);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(history ->
                        embeddingClient.getEmbedding(userQuery)
                                .flatMapMany(vector ->
                                        // 3. 시맨틱 캐시(기존 답변)가 있는지 확인
                                        pineconeApiClient.checkSemanticCache(vector)
                                                .defaultIfEmpty(new SimilarityResult(null, 0.0))
                                                .flatMapMany(result -> {
                                                    String answer = result.answer();
                                                    Double score = result.score();

                                                    if (result.answer() != null && score >= 0.95) {
                                                        log.info("시맨틱 캐시 적중! 기존 답변 재활용: {}", answer);
                                                        langfuseApiClient.logGeneration("상담[Cache] : ", userQuery, answer, score);
                                                        // 캐시 적중 시에도 히스토리에 답변 저장
                                                        return Mono.fromRunnable(() -> chatHistoryService.saveMessage(sessionId, "assistant", answer))
                                                                .subscribeOn(Schedulers.boundedElastic())
                                                                .thenMany(Flux.just(AiResponse.from(answer)));
                                                    }
                                                    log.info("캐시 미적중(유사도: {}), Groq을 호출", score);
                                                    return runGroqFlow(sessionId, userQuery, history, vector, score);
                                                })
                                )
                );
    }

    private Flux<AiResponse> runGroqFlow(String sessionId, String userQuery, List<Map<String, Object>> history, List<Double> vector, Double score) {
        StringBuilder fullAiResponse = new StringBuilder();

        return pineconeApiClient.retrieveContext(vector)
                .flatMapMany(context -> groqApiClient.askGroqWithContext(context, userQuery, history))
                .doOnNext(response -> {
                    if (response.getAnswer() != null) {
                        fullAiResponse.append(response.getAnswer());
                    }
                })
                .publishOn(Schedulers.boundedElastic())
                .doOnComplete(() -> {
                    String finalAnswer = fullAiResponse.toString().trim();
                    langfuseApiClient.logGeneration("상담[AI] : ", userQuery, finalAnswer, score);

                    // 답변이 비어있거나, 에러 메시지인 경우에는 저장하지 않음
                    if (!finalAnswer.isEmpty() &&
                            !finalAnswer.contains("원활하지 않습니다") &&
                            !finalAnswer.contains("서비스 연결")) {

                        chatHistoryService.saveMessage(sessionId, "assistant", finalAnswer);

                        String cacheId = UUID.randomUUID().toString();
                        pineconeApiClient.upsertCache(cacheId, vector, userQuery, finalAnswer).subscribe();

                        log.info("AI 최종 응답 저장 완료 (세션: {})", sessionId);
                    } else {
                        log.warn("⚠️ 부적절한 응답(에러 등)으로 인해 히스토리/캐시 저장을 건너뜁니다.");
                    }
                });
    }
}