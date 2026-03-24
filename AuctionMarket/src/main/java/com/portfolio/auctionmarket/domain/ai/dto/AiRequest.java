package com.portfolio.auctionmarket.domain.ai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiRequest {
    private String sessionId;
    private String question;
}