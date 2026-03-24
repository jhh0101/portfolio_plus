package com.portfolio.auctionmarket.domain.products.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import com.portfolio.auctionmarket.domain.ratings.entity.Rating;
import com.portfolio.auctionmarket.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {

    private Long productId;
    private String seller;
    private Long sellerId;
    private String category;
    private Long categoryId;
    private String title;
    private String description;
    private Integer viewCount;
    private Double ratingScore;
    private ProductStatus productStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static ProductDetailResponse from(Product entity) {
        return from(entity, null);
    }

    public static ProductDetailResponse from(Product entity, User user) {
        return ProductDetailResponse.builder()
                .productId(entity.getProductId())
                .seller(entity.getSeller().getNickname())
                .sellerId(entity.getSeller().getUserId())
                .category(entity.getCategory().getCategory())
                .categoryId(entity.getCategory().getCategoryId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .viewCount(entity.getViewCount())
                .productStatus(entity.getProductStatus())
                .createdAt(entity.getCreatedAt())
                .ratingScore(user != null ? user.getAvgRating() : 0.0)
                .build();
    }
}
