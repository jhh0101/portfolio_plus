package org.example.auctioncommon.domain.rating.repository

import org.example.auctioncommon.domain.order.entity.Order
import org.example.auctioncommon.domain.rating.entity.Rating
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RatingRepository : JpaRepository<Rating, Long> {
    fun existsByOrder(order: Order): Boolean

    @EntityGraph(attributePaths = ["toUser", "fromUser", "order"])
    fun findAllByToUser_UserId(toUserUserId: Long, pageable: Pageable): Page<Rating>

    fun findByOrder_OrderId(orderId: Long): Rating?
}
