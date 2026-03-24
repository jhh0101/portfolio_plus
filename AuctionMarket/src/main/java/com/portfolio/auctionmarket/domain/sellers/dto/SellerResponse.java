package com.portfolio.auctionmarket.domain.sellers.dto;

import com.portfolio.auctionmarket.domain.sellers.entity.Seller;
import com.portfolio.auctionmarket.domain.sellers.entity.SellerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerResponse {
    private Long sellerId;
    private String nickname;
    private String storeName;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private SellerStatus status;
    private String rejectReason;

    public static SellerResponse from(Seller entity) {
        return SellerResponse.builder()
                .sellerId(entity.getSellerId())
                .nickname(entity.getUser().getNickname())
                .storeName(entity.getStoreName())
                .bankName(entity.getBankName())
                .accountNumber(entity.getAccountNumber())
                .accountHolder(entity.getAccountHolder())
                .status(entity.getStatus())
                .rejectReason(entity.getRejectReason())
                .build();
    }
}
