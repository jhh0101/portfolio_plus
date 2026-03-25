package org.example.auctioncommon.domain.order.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.auctioncommon.domain.auction.entity.QAuction.auction
import org.example.auctioncommon.domain.order.entity.Order
import org.example.auctioncommon.domain.order.entity.QOrder.order
import org.example.auctioncommon.domain.product.entity.QProduct.product
import org.example.auctioncommon.domain.user.entity.QUser.user
import org.springframework.stereotype.Repository

@Repository
class OrderQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {

    fun findOrderWithProductAndSeller(orderId: Long): Order? {
        return jpaQueryFactory.selectFrom(order)
                .innerJoin(order.auction, auction).fetchJoin()
                .innerJoin(auction.product, product).fetchJoin()
                .innerJoin(product.seller, user).fetchJoin()
                .where(order.orderId.eq(orderId))
                .fetchOne()

    }
}
