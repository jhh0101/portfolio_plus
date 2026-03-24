package com.portfolio.auctionmarket.domain.orders.repository;

import com.portfolio.auctionmarket.domain.orders.entity.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.portfolio.auctionmarket.domain.auctions.entity.QAuction.auction;
import static com.portfolio.auctionmarket.domain.orders.entity.QOrder.order;
import static com.portfolio.auctionmarket.domain.products.entity.QProduct.product;
import static com.portfolio.auctionmarket.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<Order> findOrderWithProductAndSeller(Long orderId) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(order)
                        .innerJoin(order.auction, auction).fetchJoin()
                        .innerJoin(auction.product, product).fetchJoin()
                        .innerJoin(product.seller, user).fetchJoin()
                        .where(order.orderId.eq(orderId))
                        .fetchOne()
        );
    }
}
