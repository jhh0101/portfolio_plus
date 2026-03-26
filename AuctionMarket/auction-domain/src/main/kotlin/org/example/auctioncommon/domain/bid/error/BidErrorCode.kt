package org.example.auctioncommon.domain.bid.error

import org.example.auctioncommon.global.error.ErrorCode

enum class BidErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    BID_NOT_FOUND("B001", "입찰 내역을 찾을 수 없습니다"),
    BID_PRICE_TOO_LOW("B002", "입찰가는 현재가보다 높아야 합니다"),
    SELF_BID_NOT_ALLOWED("B003", "자신의 상품에는 입찰할 수 없습니다"),
    NOT_ENOUGH_POINTS("B004", "포인트가 부족합니다"),
    ALREADY_HIGHEST_BIDDER("B005", "이미 입찰 중 입니다."),
    BID_CANCEL_RESTRICTED("B006", "입찰 후 10분이 지났거나 경매 마감 10분 전에는 취소가 불가능합니다"),
}