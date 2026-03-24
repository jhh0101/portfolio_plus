package com.portfolio.auctionmarket.domain.orders.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.portfolio.auctionmarket.domain.orders.entity.Order;
import com.portfolio.auctionmarket.domain.products.dto.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private String nickname;
    private Long finalPrice;

    @JsonUnwrapped
    private ProductResponse productResponse;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    public static OrderResponse from(Order entity) {
        if (entity == null) {
            return null;
        }
        return OrderResponse.builder()
                .orderId(entity.getOrderId())
                .nickname(entity.getBuyer().getNickname())
                .finalPrice(entity.getFinalPrice())
                .endTime(entity.getAuction().getEndTime())
                .productResponse(ProductResponse.from(entity.getAuction().getProduct()))
                .build();
    }
}
