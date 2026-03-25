package org.example.auctioncommon.domain.bid.dto

import com.fasterxml.jackson.annotation.JsonFormat
import org.example.auctioncommon.domain.bid.entity.BidStatus
import java.time.LocalDateTime

class BidInfoImpl(
    val bidId: Long? = null,
    val auctionId: Long? = null,
    val nickname: String? = null,
    val bidPrice: Long? = null,
    val status: BidStatus? = null,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val bidTime: LocalDateTime? = null
) {




    companion object {
        fun from(response: BidInfo): BidInfoImpl {
            return BidInfoImpl(
                bidId = response.bidId,
                auctionId = response.auctionId,
                nickname = response.nickname,
                bidPrice = response.bidPrice,
                status = response.status?.let { BidStatus.valueOf(it) },
                bidTime = response.bidTime
            )
        }
    }
}
