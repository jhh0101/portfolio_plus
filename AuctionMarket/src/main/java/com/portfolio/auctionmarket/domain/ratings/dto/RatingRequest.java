package com.portfolio.auctionmarket.domain.ratings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    private Integer score;
    private String comment;
}
