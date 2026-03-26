package org.example.auctioncommon.domain.bid.dto

import org.example.auctioncommon.domain.bid.entity.Bid

data class BidResultResponse(
    val auctionId: Long,
    val title: String,
    val price: Long,
    val currentPoint: Long,
)

fun Bid.toDto(): BidResultResponse {
    return BidResultResponse(
        auctionId = this.auction?.auctionId ?: 0L,
        title = this.auction?.product?.title ?: "제목이 없습니다.",
        price = this.bidPrice,
        currentPoint = this.bidder?.point ?: 0L
    )
}