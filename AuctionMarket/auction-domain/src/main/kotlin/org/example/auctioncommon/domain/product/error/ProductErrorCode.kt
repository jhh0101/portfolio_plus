package org.example.auctioncommon.domain.product.error

import org.example.auctioncommon.global.error.ErrorCode

enum class ProductErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    PRODUCT_NOT_FOUND("PRODUCT001", "상품을 찾을 수 없습니다"),
    CANNOT_MODIFY_AFTER_BID("PRODUCT002", "입찰한 상품은 수정할 수 없습니다"),
    CANNOT_DELETE_AFTER_BID("PRODUCT003", "입찰한 상품은 삭제할 수 없습니다"),
}