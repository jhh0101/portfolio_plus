package com.portfolio.auctionmarket.domain.bids.dto;

import com.portfolio.auctionmarket.domain.bids.entity.BidStatus;

import java.time.LocalDateTime;

public interface BidResponse {

    Long getBidId();
    Long getAuctionId();
    String getNickname();
    Long getBidPrice();
    LocalDateTime getBidTime();
    String getStatus();


}