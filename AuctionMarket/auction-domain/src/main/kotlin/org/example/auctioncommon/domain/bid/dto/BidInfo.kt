package org.example.auctioncommon.domain.bid.dto

import java.time.LocalDateTime

interface BidInfo {
    val bidId: Long?
    val auctionId: Long?
    val nickname: String?
    val bidPrice: Long?
    val bidTime: LocalDateTime?
    val status: String?
}