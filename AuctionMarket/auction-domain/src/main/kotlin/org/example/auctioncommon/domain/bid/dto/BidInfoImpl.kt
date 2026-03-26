package org.example.auctioncommon.domain.bid.dto

import com.fasterxml.jackson.annotation.JsonFormat
import org.example.auctioncommon.domain.bid.entity.BidStatus
import java.time.LocalDateTime

class BidInfoImpl(
    val bidId: Long,
    val auctionId: Long,
    val nickname: String,
    val bidPrice: Long,
    val status: BidStatus,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val bidTime: LocalDateTime? = null
)

fun BidInfo.toDto(): BidInfoImpl {
    return BidInfoImpl(
        bidId = this.bidId,
        auctionId = this.auctionId,
        nickname = this.nickname,
        bidPrice = this.bidPrice,
        status = this.status,
        bidTime = this.bidTime
    )
}
