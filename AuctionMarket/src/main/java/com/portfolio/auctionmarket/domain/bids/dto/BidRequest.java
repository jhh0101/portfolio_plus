package com.portfolio.auctionmarket.domain.bids.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BidRequest {

    @NotNull(message = "입찰가를 입력해주세요.")
    private Long bidPrice;
}
