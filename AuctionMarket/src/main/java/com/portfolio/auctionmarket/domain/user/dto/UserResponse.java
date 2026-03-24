package com.portfolio.auctionmarket.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.auctionmarket.domain.sellers.entity.Seller;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.sellers.entity.SellerStatus;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.entity.UserStatus;
import com.portfolio.auctionmarket.global.util.MaskingUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String email;
    private String username;
    private String phone;
    private String nickname;
    private String baseAddress;
    private String detailAddress;
    private Role role;
    private SellerStatus sellerStatus;
    private UserStatus userStatus;
    private Long point;
    private String avgRating;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static UserResponse from(User user){
        String formatPhone = MaskingUtil.formatPhone(user.getPhone());
        Seller seller = user.getSeller();
        SellerStatus sellerStatus = (seller != null) ? seller.getStatus() : SellerStatus.NONE;

        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(formatPhone)
                .nickname(user.getNickname())
                .baseAddress(user.getBaseAddress())
                .detailAddress(user.getDetailAddress())
                .role(user.getRole())
                .userStatus(user.getStatus())
                .point(user.getPoint())
                .avgRating(String.format("%.1f", user.getAvgRating()))
                .sellerStatus(sellerStatus)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
