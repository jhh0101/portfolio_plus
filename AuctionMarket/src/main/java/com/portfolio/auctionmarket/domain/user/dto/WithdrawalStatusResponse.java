package com.portfolio.auctionmarket.domain.user.dto;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import com.portfolio.auctionmarket.domain.user.entity.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WithdrawalStatusResponse {

    private String nickname;
    private Long currentPoint;
    private Long bidCount;
    private Long productCount;

    public static WithdrawalStatusResponse from(User user, Long bidCount, Long productCount) {
        return WithdrawalStatusResponse.builder()
                .nickname(user.getNickname())
                .currentPoint(user.getPoint())
                .bidCount(bidCount)
                .productCount(productCount)
                .build();
    }
}
