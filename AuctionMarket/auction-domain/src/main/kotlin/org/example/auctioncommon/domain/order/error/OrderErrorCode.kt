package org.example.auctioncommon.domain.order.error

import org.example.auctioncommon.global.error.ErrorCode

enum class OrderErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    ORDER_NOT_FOUND("ORDER001", "주문 내역을 찾을 수 없습니다"),
}