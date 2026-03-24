package com.portfolio.auctionmarket.domain.products.repository;


import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.products.dto.ProductListCondition;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.portfolio.auctionmarket.domain.auctions.entity.QAuction.auction;
import static com.portfolio.auctionmarket.domain.bids.entity.QBid.bid;
import static com.portfolio.auctionmarket.domain.products.entity.QProduct.product;
import static com.portfolio.auctionmarket.domain.categories.entity.QCategory.category1;
import static com.portfolio.auctionmarket.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;;

    public Page<Product> productList(Long userId, ProductListCondition condition, Pageable pageable) {
        List<Product> content = jpaQueryFactory
                .selectFrom(product)
                .innerJoin(product.seller, user).fetchJoin()
                .innerJoin(product.auction, auction).fetchJoin()
                .leftJoin(product.category, category1).fetchJoin()
                .where(
                        isMyAuction(userId),
                        titleContain(condition.title()),
                        pathStartWith(condition.path()),
                        statusFilter(userId),
                        auction.status.eq(AuctionStatus.PROCEEDING),
                        product.productStatus.eq(ProductStatus.ACTIVE)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier(condition.sort()))
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(product.count())
                .from(product)
                .innerJoin(product.seller, user)
                .innerJoin(product.auction, auction)
                .leftJoin(product.category, category1)
                .where(
                        isMyAuction(userId),
                        titleContain(condition.title()),
                        pathStartWith(condition.path()),
                        statusFilter(userId),
                        auction.status.eq(AuctionStatus.PROCEEDING),
                        product.productStatus.eq(ProductStatus.ACTIVE)
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    public Slice<Product> adminProductList(Long userId, ProductListCondition condition, Pageable pageable) {

        int pageSize = pageable.getPageSize();

        List<Product> content = jpaQueryFactory
                .selectFrom(product)
                .innerJoin(product.seller, user).fetchJoin()
                .innerJoin(product.auction, auction).fetchJoin()
                .leftJoin(product.category, category1).fetchJoin()
                .where(
                        isMyAuction(userId),
                        titleContain(condition.title()),
                        pathStartWith(condition.path()),
                        statusFilter(userId)
                )
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .orderBy(orderSpecifier(condition.sort()))
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageSize) {
            content.remove(pageSize);
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression isMyAuction(Long userId) {
        return userId != null ? product.seller.userId.eq(userId) : null;
    }
    private BooleanExpression titleContain(String title) {
        return StringUtils.hasText(title) ? product.title.contains(title) : null;
    }
    private BooleanExpression pathStartWith(String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }

        return product.category.path.eq(path)
                .or(product.category.path.startsWith(path + "/"));
    }

    private BooleanExpression statusFilter(Long userId) {
        if (userId == null) {
            return null;
        }
        return product.productStatus.in(ProductStatus.ACTIVE, ProductStatus.SOLD, ProductStatus.FAILED);
    }

    private OrderSpecifier<?> orderSpecifier(String sort) {
        if ("endingSoon".equals(sort)) {
            return product.auction.endTime.asc();
        } else if ("priceHigh".equals(sort)) {
            return product.auction.currentPrice.desc();
        } else if ("priceLow".equals(sort)) {
            return product.auction.currentPrice.asc();
        }
        return product.productId.desc();
    }
}
