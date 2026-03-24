package com.portfolio.auctionmarket.domain.sellers.dto;

import com.portfolio.auctionmarket.domain.sellers.entity.Seller;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RejectReasonResponse {
    private String rejectReason;

    public static RejectReasonResponse from(Seller entity) {
        return RejectReasonResponse.builder()
                .rejectReason(entity.getRejectReason())
                .build();
    }
}
