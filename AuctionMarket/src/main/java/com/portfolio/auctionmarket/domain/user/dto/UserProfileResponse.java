package com.portfolio.auctionmarket.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.auctionmarket.domain.sellers.entity.Seller;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.sellers.entity.SellerStatus;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.global.util.MaskingUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private Long userId;
    private String email;
    private String username;
    private String phone;
    private String nickname;
    private String baseAddress;
    private String detailAddress;
    private Role role;
    private Long point;
    private String avgRating;
    private SellerStatus sellerStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static UserProfileResponse from(User user){
        String formatPhone = MaskingUtil.formatPhone(user.getPhone());
        Seller seller = user.getSeller();
        SellerStatus sellerStatus = (seller != null) ? seller.getStatus() : SellerStatus.NONE;

        String detailAddress = user.getDetailAddress() != null ? user.getDetailAddress() : "";

        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(formatPhone)
                .nickname(user.getNickname())
                .baseAddress(user.getBaseAddress())
                .detailAddress(detailAddress)
                .role(user.getRole())
                .point(user.getPoint())
                .avgRating(String.format("%.1f", user.getAvgRating()))
                .sellerStatus(sellerStatus)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
