package com.portfolio.auctionmarket.domain.products.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long productId;
    private String seller;
    private String category;
    private String title;
    private ProductStatus productStatus;
    private String mainImageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static ProductResponse from(Product entity) {
        String mainUrl = entity.getImage().stream()
                .filter(img -> img.getImageOrder().equals(1))
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse("https://picsum.photos/400/300");

        return ProductResponse.builder()
                .productId(entity.getProductId())
                .seller(entity.getSeller().getNickname())
                .category(entity.getCategory().getCategory())
                .title(entity.getTitle())
                .productStatus(entity.getProductStatus())
                .mainImageUrl(mainUrl)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
