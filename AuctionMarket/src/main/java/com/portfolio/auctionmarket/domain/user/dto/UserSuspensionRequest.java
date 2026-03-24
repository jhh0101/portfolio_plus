package com.portfolio.auctionmarket.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSuspensionRequest {

    private String suspensionReason;
}
