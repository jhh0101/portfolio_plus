package org.example.auctioncommon.domain.bid.dto

import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.example.auctioncommon.domain.product.dto.ProductAndAuctionResponse

class BidHistoryResponse(
    @JsonUnwrapped
    private val response: ProductAndAuctionResponse? = null,
    private val myMaxBidPrice: Long? = null
) {

}
