package com.portfolio.auctionmarket.domain.ratings.dto;

import com.portfolio.auctionmarket.domain.ratings.entity.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponse {
    private Long ratingId;
    private String toNickname;
    private String fromNickname;
    private String title;
    private Integer score;
    private String comment;

    public static RatingResponse from(Rating entity) {
        return RatingResponse.builder()
                .ratingId(entity.getRatingId())
                .toNickname(entity.getToUser().getNickname())
                .fromNickname(entity.getFromUser().getNickname())
                .title(entity.getOrder().getTitle())
                .score(entity.getScore())
                .comment(entity.getComment())
                .build();
    }
}
