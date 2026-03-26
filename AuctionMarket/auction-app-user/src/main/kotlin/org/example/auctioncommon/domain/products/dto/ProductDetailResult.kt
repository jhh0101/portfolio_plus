package org.example.auctioncommon.domain.products.dto

data class ProductDetailResult(
    val response: ProductDetailAndAuctionResponse,
    val newCookieValue: String?
)