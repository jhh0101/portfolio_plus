package com.portfolio.auctionmarket.domain.ratings.dto;

import com.portfolio.auctionmarket.domain.ratings.entity.Rating;
import com.portfolio.auctionmarket.domain.ratings.entity.RatingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDeleteResponse {
    private String nickname;
    private RatingStatus status;

    public static RatingDeleteResponse from(Rating entity) {
        return RatingDeleteResponse.builder()
                .nickname(entity.getToUser().getNickname())
                .status(entity.getStatus())
                .build();
    }
}
