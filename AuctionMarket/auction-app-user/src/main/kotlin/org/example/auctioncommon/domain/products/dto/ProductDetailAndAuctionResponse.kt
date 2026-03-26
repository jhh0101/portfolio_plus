package org.example.auctioncommon.domain.products.dto

import org.example.auctioncommon.domain.auction.dto.AuctionResponse
import org.example.auctioncommon.domain.auction.dto.toAuctionResponse
import org.example.auctioncommon.domain.product.entity.Product
import org.example.auctioncommon.domain.user.entity.User

data class ProductDetailAndAuctionResponse(
    val productDetailResponse: ProductDetailResponse,
    val auctionResponse: AuctionResponse
)
fun Product.toProductDetailAndAuctionDto(user: User?) : ProductDetailAndAuctionResponse{
    return ProductDetailAndAuctionResponse(
        productDetailResponse = this.toProductDetailDto(user),
        auctionResponse = this.auction.toAuctionResponse()
    )
}