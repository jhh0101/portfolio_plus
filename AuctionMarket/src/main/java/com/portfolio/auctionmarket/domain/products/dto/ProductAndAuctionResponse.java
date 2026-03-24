package com.portfolio.auctionmarket.domain.products.dto;

import com.portfolio.auctionmarket.domain.auctions.dto.AuctionResponse;
import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAndAuctionResponse {
    private ProductResponse productResponse;

    private AuctionResponse auctionResponse;

    public static ProductAndAuctionResponse from(Product entity) {
        return ProductAndAuctionResponse.builder()
                .productResponse(ProductResponse.from(entity))
                .auctionResponse(AuctionResponse.from(entity.getAuction()))
                .build();
    }
}
