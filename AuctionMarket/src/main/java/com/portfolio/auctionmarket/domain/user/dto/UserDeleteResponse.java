package com.portfolio.auctionmarket.domain.user.dto;

import com.portfolio.auctionmarket.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDeleteResponse {
    private String email;

    public static UserDeleteResponse from(User entity) {
        return UserDeleteResponse.builder()
                .email(entity.getEmail())
                .build();
    }
}
