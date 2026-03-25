package org.example.auctioncommon.domain.order.repository

import org.example.auctioncommon.domain.order.entity.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = ["auction", "auction.product", "auction.product.image"])
    fun findAllByBuyer_UserId(userId: Long, pageable: Pageable): Page<Order>

    fun findByAuction_AuctionId(auctionAuctionId: Long): Order?
}
