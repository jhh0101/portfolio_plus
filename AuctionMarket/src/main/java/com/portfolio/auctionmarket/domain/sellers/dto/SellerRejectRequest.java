package com.portfolio.auctionmarket.domain.sellers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SellerRejectRequest {

    @NotBlank(message = "거절 사유를 입력해주세요.")
    private String rejectReason;
}
