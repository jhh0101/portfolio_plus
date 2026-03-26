package org.example.auctioncommon.domain.orders.service

import org.example.auctioncommon.domain.order.entity.Order
import org.example.auctioncommon.domain.order.error.OrderErrorCode
import org.example.auctioncommon.domain.order.repository.OrderRepository
import org.example.auctioncommon.domain.orders.dto.OrderResponse
import org.example.auctioncommon.domain.orders.dto.toDto
import org.example.auctioncommon.global.error.CustomException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService(
    private val orderRepository: OrderRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun findOrder(userId: Long, pageable: Pageable): Page<OrderResponse> {
        val orders: Page<Order> = orderRepository.findAllByBuyer_UserId(userId, pageable)
        log.info("사용자 {}의 낙찰 내역 조회 요청", userId)
        return orders.map { it.toDto() }
    }

    fun auctionOrder(auctionId: Long): OrderResponse {
        val order: Order = orderRepository.findByAuction_AuctionId(auctionId)
            ?: throw CustomException(OrderErrorCode.ORDER_NOT_FOUND)

        log.info("옥션 {}의 낙찰자 조회", auctionId)
        return order.toDto()
    }
}
