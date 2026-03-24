package com.portfolio.auctionmarket.domain.products.dto;

import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponse {
    private Long imageId;
    private Long productId;
    private String imageUrl;
    private Integer imageOrder;


    public static ProductImageResponse from(ProductImage entity) {

        return ProductImageResponse.builder()
                .imageId(entity.getImageId())
                .productId(entity.getProduct().getProductId())
                .imageUrl(entity.getImageUrl())
                .imageOrder(entity.getImageOrder())
                .build();
    }
}
