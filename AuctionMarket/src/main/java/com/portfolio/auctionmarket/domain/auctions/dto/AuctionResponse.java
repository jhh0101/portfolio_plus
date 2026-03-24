package com.portfolio.auctionmarket.domain.auctions.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionResponse {

    private Long auctionId;

    private Long startPrice;

    private Long currentPrice;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    private AuctionStatus status;

    public static AuctionResponse from(Auction entity) {
        return AuctionResponse.builder()
                .auctionId(entity.getAuctionId())
                .startPrice(entity.getStartPrice())
                .currentPrice(entity.getCurrentPrice())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .build();
    }
}
