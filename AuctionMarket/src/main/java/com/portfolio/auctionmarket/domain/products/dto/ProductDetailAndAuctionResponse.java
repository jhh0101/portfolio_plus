package com.portfolio.auctionmarket.domain.products.dto;

import com.portfolio.auctionmarket.domain.auctions.dto.AuctionResponse;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.ratings.entity.Rating;
import com.portfolio.auctionmarket.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailAndAuctionResponse {
    private ProductDetailResponse productDetailResponse;

    private AuctionResponse auctionResponse;

    public static ProductDetailAndAuctionResponse from(Product entity) {
        return from(entity, null);
    }

    public static ProductDetailAndAuctionResponse from(Product entity, User user) {
        return ProductDetailAndAuctionResponse.builder()
                .productDetailResponse(ProductDetailResponse.from(entity, user))
                .auctionResponse(AuctionResponse.from(entity.getAuction()))
                .build();
    }
}
