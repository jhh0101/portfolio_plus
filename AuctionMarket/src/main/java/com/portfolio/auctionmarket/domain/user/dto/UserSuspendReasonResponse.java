package com.portfolio.auctionmarket.domain.user.dto;

import com.portfolio.auctionmarket.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSuspendReasonResponse {

    private String suspendReason;

    public static UserSuspendReasonResponse from(User user) {
        return UserSuspendReasonResponse.builder()
                .suspendReason(user.getSuspensionReason())
                .build();
    }
}
