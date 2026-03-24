package com.portfolio.auctionmarket.domain.ai.controller;

import com.portfolio.auctionmarket.domain.ai.dto.AiRequest;
import com.portfolio.auctionmarket.domain.ai.dto.AiResponse;
import com.portfolio.auctionmarket.domain.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groq")
public class GroqController {
    private final AiService aiService;

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AiResponse> chatAI(@RequestBody AiRequest request) {
        return aiService.askAuctionBot(request.getSessionId(), request.getQuestion());
    }
}
