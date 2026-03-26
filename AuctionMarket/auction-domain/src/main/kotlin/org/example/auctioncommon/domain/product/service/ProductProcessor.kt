package org.example.auctioncommon.domain.product.service

import org.springframework.stereotype.Service

@Service
class ProductProcessor {

    fun validateFindProductDetail(productId: Long, isSeller: Boolean, viewedCookieValue: String?
    ): Pair<Boolean, String?> {

        if (isSeller) return Pair(false, null)

        val hasViewed = viewedCookieValue?.contains("[$productId]") == true
        if (hasViewed) return Pair(false, null)

        // 조회수를 올려야 하는 상황! 새 쿠키 문자열 생성
        val newCookieValue = if (viewedCookieValue != null) {
            "${viewedCookieValue}_[$productId]"
        } else {
            "[$productId]"
        }

        return Pair(true, newCookieValue)
    }

}
