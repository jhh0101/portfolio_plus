package com.portfolio.auctionmarket.domain.ratings.repository;

import com.portfolio.auctionmarket.domain.ratings.entity.Rating;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.function.AvgFunction;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.portfolio.auctionmarket.domain.auctions.entity.QAuction.auction;
import static com.portfolio.auctionmarket.domain.orders.entity.QOrder.order;
import static com.portfolio.auctionmarket.domain.products.entity.QProduct.product;
import static com.portfolio.auctionmarket.domain.ratings.entity.QRating.rating;

@Repository
@RequiredArgsConstructor
public class RatingQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;;

    public Optional<Rating> findById(Long ratingId) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(rating)
                .innerJoin(rating.fromUser).fetchJoin()
                .innerJoin(rating.toUser).fetchJoin()
                .innerJoin(rating.order, order).fetchJoin()
                .innerJoin(order.auction, auction).fetchJoin()
                .innerJoin(auction.product, product).fetchJoin()
                .where(rating.ratingId.eq(ratingId))
                .fetchOne()
        );
    }

    public Double avgRating(Long userId) {
        Double result = jpaQueryFactory
                .select(rating.score.avg())
                .from(rating)
                .where(rating.toUser.userId.eq(userId))
                .fetchOne();

        return result != null ? result : 0.0;
    }
}
