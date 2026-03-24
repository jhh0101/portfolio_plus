package com.portfolio.auctionmarket.domain.sellers.dto;

import com.portfolio.auctionmarket.domain.sellers.entity.Seller;
import com.portfolio.auctionmarket.domain.sellers.entity.SellerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerApplyListResponse {
    private Long sellerId;
    private String email;
    private String nickname;
    private String storeName;
    private LocalDateTime appliedAt;

    public static SellerApplyListResponse from(Seller entity) {
        return SellerApplyListResponse.builder()
                .sellerId(entity.getSellerId())
                .email(entity.getUser().getEmail())
                .nickname(entity.getUser().getNickname())
                .storeName(entity.getStoreName())
                .appliedAt(entity.getCreatedAt())
                .build();
    }

}
