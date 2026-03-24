package com.portfolio.auctionmarket.domain.bids.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.auctionmarket.domain.bids.entity.BidStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidResponseImpl {

    private Long bidId;
    private Long auctionId;
    private String nickname;
    private Long bidPrice;
    private BidStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bidTime;

    public static BidResponseImpl from(BidResponse response) {
        return BidResponseImpl.builder()
                .bidId(response.getBidId())
                .auctionId(response.getAuctionId())
                .nickname(response.getNickname())
                .bidPrice(response.getBidPrice())
                .bidTime(response.getBidTime())
                .status(BidStatus.valueOf(response.getStatus()))
                .build();
    }
}
