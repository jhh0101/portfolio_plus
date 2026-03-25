package org.example.auctioncommon.domain.rating.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.auctioncommon.domain.auction.entity.QAuction.auction
import org.example.auctioncommon.domain.order.entity.QOrder.order
import org.example.auctioncommon.domain.product.entity.QProduct.product
import org.example.auctioncommon.domain.rating.entity.QRating.rating
import org.example.auctioncommon.domain.rating.entity.Rating
import org.springframework.stereotype.Repository

@Repository
class RatingQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {

    fun findById(ratingId: Long): Rating? {
        return jpaQueryFactory
                .selectFrom(rating)
                .innerJoin(rating.fromUser).fetchJoin()
                .innerJoin(rating.toUser).fetchJoin()
                .innerJoin(rating.order, order).fetchJoin()
                .innerJoin(order.auction, auction).fetchJoin()
                .innerJoin(auction.product, product).fetchJoin()
                .where(rating.ratingId.eq(ratingId))
                .fetchOne()
    }

    fun avgRating(userId: Long): Double {
        return jpaQueryFactory
            .select(rating.score.avg())
            .from(rating)
            .where(rating.toUser.userId.eq(userId))
            .fetchOne() ?: 0.0
    }
}
