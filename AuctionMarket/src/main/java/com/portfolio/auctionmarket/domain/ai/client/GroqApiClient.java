package com.portfolio.auctionmarket.domain.ai.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.auctionmarket.domain.ai.dto.AiResponse;
import com.portfolio.auctionmarket.domain.ai.dto.GroqResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GroqApiClient {
    private final WebClient groqWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public GroqApiClient(@Qualifier("groqWebClient") WebClient webClient) {
        this.groqWebClient = webClient;
    }

    public Flux<AiResponse> askGroqWithContext(String context, String userMessage, List<Map<String, Object>> history) {
        String systemPrompt =
                "당신은 '옥션마켓'의 친절하고 전문적인 고객센터 상담원입니다.\n" +
                        "제공된 [참고 지식]을 유연하게 활용하여 고객의 질문에 대답해 주세요.\n\n" +
                        "[답변 원칙]\n" +
                        "1. 자연스러운 대화: 기계적인 표현을 피하고 직접 안내하듯 말하세요.\n" +
                        "2. 적극적인 답변: 지식에 관련 단서가 있다면 최대한 친절하게 답변하세요.\n" +
                        "3. 환각 금지: 지식에 없는 내용은 절대 임의로 상상하지 마세요.\n" +
                        "4. 모든 답변은 초등학생도 이해할 수 있는 쉬운 현대 한국어 구어체로 작성하세요. 어려운 한자어나 전문 용어 대신 일상적인 단어를 사용하세요.\n" +
                        "5. 만약 질문이 '옥션마켓' 서비스와 전혀 관련이 없거나 [참고 지식]에 없는 내용이라면, '죄송하지만 해당 질문은 서비스 안내 범위를 벗어납니다. 옥션마켓 이용에 관해 궁금한 점을 말씀해 주세요.'라고 정중히 안내하세요.\n" +
                        "6. 출력 언어 엄격 제한 (매우 중요): > - 모든 답변은 100% 순수 한글로만 작성하세요.\n" +
                        "- [참고 지식] 원본에 한자(漢字)가 포함되어 있더라도, 출력할 때는 반드시 한글로 번역/변환해서 대답해야 합니다.\n"+
                        "- (❌ 잘못된 예: 仔細히 읽어보세요 -> ⭕ 올바른 예: 자세히 읽어보세요)\n"+
                        "7. 제공된 정보에 사용자의 질문과 일치하는 기능(예: 출금)이 명시되어 있다면, 일반적인 상식보다 제공된 정보를 우선하여 답변하라. 확실하지 않은 부정적인 답변은 지양하라.\n\n"+
                        "[참고 지식]:\n" + context;

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));

        String lastRole = "system";

        if (history != null && !history.isEmpty()) {
            for (Map<String, Object> h : history) {
                String role = String.valueOf(h.get("role")).toLowerCase();
                String content = (h.get("content") != null) ? String.valueOf(h.get("content")).trim() : "";

                if (content.isEmpty() || role.equals("system")) continue;

                if (role.equals(lastRole)) {
                    int lastIdx = messages.size() - 1;
                    String prevContent = messages.get(lastIdx).get("content");
                    if (!prevContent.contains(content)) {
                        messages.set(lastIdx, Map.of("role", role, "content", prevContent + "\n" + content));
                    }
                } else {
                    messages.add(Map.of("role", role, "content", content));
                    lastRole = role;
                }
            }
        }

        String finalUserMessage = (userMessage != null && !userMessage.isBlank()) ? userMessage.trim() : "질문 없음";
        if ("user".equals(lastRole)) {
            int lastIdx = messages.size() - 1;
            String prevContent = messages.get(lastIdx).get("content");
            if (!prevContent.contains(finalUserMessage)) {
                messages.set(lastIdx, Map.of("role", "user", "content", prevContent + "\n" + finalUserMessage));
            }
        } else {
            messages.add(Map.of("role", "user", "content", finalUserMessage));
        }

        // 💡 현존 최강 모델 유지 (글자 씹힘은 모델 잘못이 아니었습니다!)
        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "temperature", 0.2,
                "messages", messages,
                "stream", true
        );

        // 🚨 핵심 해결책: Spring의 ServerSentEvent를 사용해서 스트리밍 데이터를 안전하게 파싱!
        return groqWebClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                .filter(sse -> sse.data() != null && !"[DONE]".equals(sse.data()))
                .map(sse -> {
                    try {
                        GroqResponse response = objectMapper.readValue(sse.data(), GroqResponse.class);
                        return response.extractContent() != null ? response.extractContent() : "";
                    } catch (Exception e) {
                        return ""; // 파싱 에러난 조각은 무시
                    }
                })
                .filter(text -> !text.isEmpty()) // 빈 조각 걸러내기
                // 🔥 [마법의 코드] 10개의 조각이 모이거나 100ms(0.1초)가 지날 때까지 기다렸다가 한 번에 묶습니다!
                .bufferTimeout(10, java.time.Duration.ofMillis(100))
                .map(chunkList -> {
                    // 모인 조각들을 하나의 완성된 문자열로 찰진 결합!
                    String combinedText = String.join("", chunkList);
                    return AiResponse.from(combinedText);
                })
                .onErrorResume(e -> {
                    log.error("Groq 스트리밍 중 에러 발생: {}", e.getMessage());
                    return Flux.just(AiResponse.from("서비스 연결이 원활하지 않습니다. 잠시 후 다시 이용해주세요."));
                });
    }
}