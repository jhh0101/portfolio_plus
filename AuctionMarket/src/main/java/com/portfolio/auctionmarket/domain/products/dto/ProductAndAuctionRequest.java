package com.portfolio.auctionmarket.domain.products.dto;

import com.portfolio.auctionmarket.domain.auctions.dto.AuctionRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAndAuctionRequest {
    @Valid
    private ProductRequest productRequest;

    @Valid
    private AuctionRequest auctionRequest;

}
