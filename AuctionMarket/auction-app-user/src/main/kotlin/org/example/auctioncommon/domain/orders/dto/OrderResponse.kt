package org.example.auctioncommon.domain.orders.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.example.auctioncommon.domain.order.entity.Order
import org.example.auctioncommon.domain.product.dto.ProductResponse
import org.example.auctioncommon.domain.product.dto.toProductResponse

data class OrderResponse(
    val orderId: Long,
    val nickname: String,
    val finalPrice: Long,

    @field:JsonUnwrapped
    val productResponse: ProductResponse,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val endTime: java.time.LocalDateTime? = null,
)
fun Order.toDto() : OrderResponse {
    return OrderResponse(
        orderId = this.orderId ?: 0L,
        nickname = this.buyer?.nickname.toString(),
        finalPrice = this.finalPrice ?: 0L,
        endTime = this.auction.endTime,
        productResponse = this.auction.product?.toProductResponse()!!
    )
}

