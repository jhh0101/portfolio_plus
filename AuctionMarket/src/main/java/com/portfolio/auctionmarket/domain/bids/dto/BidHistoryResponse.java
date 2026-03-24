package com.portfolio.auctionmarket.domain.bids.dto;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.portfolio.auctionmarket.domain.products.dto.ProductAndAuctionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidHistoryResponse {

    @JsonUnwrapped
    private ProductAndAuctionResponse response;
    private Long myMaxBidPrice;

}
