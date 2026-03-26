package org.example.auctioncommon.domain.auction.error

import org.example.auctioncommon.global.error.ErrorCode

enum class AuctionErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    AUCTION_NOT_FOUND("A001", "경매를 찾을 수 없습니다"),
    AUCTION_ENDED("A002", "이미 종료된 경매입니다"),
    INVALID_AUCTION_TIME("A003", "지금은 경매 시간이 아닙니다"),
    INVALID_AUCTION_INFO("A004", "경매 정보가 올바르지 않습니다"),
}