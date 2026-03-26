package org.example.auctioncommon.domain.bid.dto

import org.jetbrains.annotations.NotNull

data class BidRequest(
    @field:NotNull(value = "입찰가를 입력해주세요.")
    val bidPrice: Long
)
