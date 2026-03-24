package com.portfolio.auctionmarket.domain.bids.dto;

import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidResultResponse {

    private Long auctionId;
    private String title;
    private Long price;
    private Long currentPoint;

    public static BidResultResponse from(Bid entity) {
        return BidResultResponse.builder()
                .auctionId(entity.getAuction().getAuctionId())
                .title(entity.getAuction().getProduct().getTitle())
                .price(entity.getBidPrice())
                .currentPoint(entity.getBidder().getPoint())
                .build();
    }
}
