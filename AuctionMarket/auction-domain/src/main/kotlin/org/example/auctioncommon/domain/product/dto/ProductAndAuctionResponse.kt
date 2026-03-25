package org.example.auctioncommon.domain.product.dto

import org.example.auctioncommon.domain.auction.dto.AuctionResponse
import org.example.auctioncommon.domain.auction.dto.toAuctionResponse
import org.example.auctioncommon.domain.product.entity.Product


data class ProductAndAuctionResponse(
    private val productResponse: ProductResponse? = null,
    private val auctionResponse: AuctionResponse? = null
)

fun Product.toDto(): ProductAndAuctionResponse {
    return ProductAndAuctionResponse(
        productResponse = this.toProductResponse(),
        auctionResponse = this.auction.toAuctionResponse()
    )
}