package com.portfolio.auctionmarket.domain.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 최근 3번(질문 3개 + 답변 3개 = 총 6개)만 기억하도록 제한
    private static final int MAX_HISTORY_SIZE = 6;
    // 세션 유지 시간 (30분 동안 대화가 없으면 기억 삭제)
    private static final Duration SESSION_TTL = Duration.ofMinutes(30);

    /**
     * 대화 내용을 Redis에 저장합니다.
     */
    public void saveMessage(String sessionId, String role, String content) {
        String key = "auction:chat:history:" + sessionId;

        try {
            String messageJson = objectMapper.writeValueAsString(Map.of("role", role, "content", content));

            redisTemplate.opsForList().rightPush(key, messageJson);

            Long size = redisTemplate.opsForList().size(key);
            if (size != null && size > MAX_HISTORY_SIZE) {
                redisTemplate.opsForList().leftPop(key);
            }

            redisTemplate.expire(key, SESSION_TTL);

        } catch (JsonProcessingException e) {
            log.error("❌ Redis 메시지 JSON 변환 에러", e);
        }
    }

    /**
     * Redis에서 이전 대화 내역을 모두 꺼내옵니다.
     */
    public List<Map<String, Object>> getHistory(String sessionId) {
        String key = "auction:chat:history:" + sessionId;
        List<String> historyJsonList = redisTemplate.opsForList().range(key, 0, -1);
        List<Map<String, Object>> history = new ArrayList<>();

        if (historyJsonList != null && !historyJsonList.isEmpty()) {
            for (String json : historyJsonList) {
                try {
                    history.add(objectMapper.readValue(json, new TypeReference<>() {}));
                } catch (JsonProcessingException e) {
                    log.error("❌ Redis 데이터 파싱 에러", e);
                }
            }
            log.info("📚 세션 [{}]의 이전 대화 {}건을 불러왔습니다.", sessionId, history.size());
        } else {
            log.info("🌱 세션 [{}]의 새로운 대화가 시작되었습니다.", sessionId);
        }

        return history;
    }
}
