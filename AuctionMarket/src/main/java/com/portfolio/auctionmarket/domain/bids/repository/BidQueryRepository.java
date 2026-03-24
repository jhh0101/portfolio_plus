package com.portfolio.auctionmarket.domain.bids.repository;

import com.portfolio.auctionmarket.domain.auctions.dto.AuctionResponse;
import com.portfolio.auctionmarket.domain.auctions.entity.QAuction;
import com.portfolio.auctionmarket.domain.bids.dto.BidHistoryResponse;
import com.portfolio.auctionmarket.domain.bids.entity.BidStatus;
import com.portfolio.auctionmarket.domain.bids.entity.QBid;
import com.portfolio.auctionmarket.domain.categories.entity.QCategory;
import com.portfolio.auctionmarket.domain.products.dto.ProductAndAuctionResponse;
import com.portfolio.auctionmarket.domain.products.dto.ProductResponse;
import com.portfolio.auctionmarket.domain.products.entity.QProduct;
import com.portfolio.auctionmarket.domain.products.entity.QProductImage;
import com.portfolio.auctionmarket.domain.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BidQueryRepository {

    QBid bid = QBid.bid;
    QAuction auction = QAuction.auction;
    QProduct product = QProduct.product;
    QUser user = QUser.user;
    QCategory category = QCategory.category1;
    QProductImage productImage = QProductImage.productImage;

    private final JPAQueryFactory jpaQueryFactory;

    public Page<BidHistoryResponse> findBidHistoryPage(Long userId, Pageable pageable) {
        List<BidHistoryResponse> content = jpaQueryFactory
                .select(Projections.constructor(BidHistoryResponse.class,
                        Projections.constructor(ProductAndAuctionResponse.class,
                                Projections.constructor(ProductResponse.class,
                                        product.productId,
                                        product.seller.nickname,
                                        product.category.category,
                                        product.title,
                                        product.productStatus,

                                        JPAExpressions
                                                .select(productImage.imageUrl)
                                                .from(productImage)
                                                .where(
                                                        productImage.product.eq(product),
                                                        productImage.imageOrder.eq(1)
                                                ),
                                        product.createdAt
                                ),
                                Projections.constructor(AuctionResponse.class,
                                        auction.auctionId,
                                        auction.startPrice,
                                        auction.currentPrice,
                                        auction.startTime,
                                        auction.endTime,
                                        auction.status
                                )
                        ),
                        bid.bidPrice.max() // 내 최고 입찰가
                ))
                .from(bid)
                .join(bid.auction, auction)
                .join(auction.product, product)
                .join(product.seller, user)
                .join(product.category, category)
                .where(bid.bidder.userId.eq(userId),
                        bid.status.eq(BidStatus.ACTIVE))
                .groupBy(
                        product.productId,
                        user.nickname,
                        category.category,
                        product.title,
                        product.productStatus,
                        product.createdAt,
                        auction.auctionId,
                        auction.startPrice,
                        auction.currentPrice,
                        auction.startTime,
                        auction.endTime,
                        auction.status
                )

                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bid.createdAt.max().desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(auction.countDistinct())
                .from(bid)
                .join(bid.auction, auction)
                .where(bid.bidder.userId.eq(userId),
                        bid.status.eq(BidStatus.ACTIVE))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    public Slice<BidHistoryResponse> findBidHistorySlice(Long userId, Pageable pageable) {

        int pageSize = pageable.getPageSize();

        List<BidHistoryResponse> content = jpaQueryFactory
                .select(Projections.constructor(BidHistoryResponse.class,
                        Projections.constructor(ProductAndAuctionResponse.class,
                                Projections.constructor(ProductResponse.class,
                                        product.productId,
                                        product.seller.nickname,
                                        product.category.category,
                                        product.title,
                                        product.productStatus,

                                        JPAExpressions
                                                .select(productImage.imageUrl)
                                                .from(productImage)
                                                .where(
                                                        productImage.product.eq(product),
                                                        productImage.imageOrder.eq(1)
                                                ),
                                        product.createdAt
                                ),
                                Projections.constructor(AuctionResponse.class,
                                        auction.auctionId,
                                        auction.startPrice,
                                        auction.currentPrice,
                                        auction.startTime,
                                        auction.endTime,
                                        auction.status
                                )
                        ),
                        new CaseBuilder()
                                .when(bid.status.eq(BidStatus.ACTIVE))
                                .then(bid.bidPrice)
                                .otherwise(0L) // 활성 입찰이 하나도 없으면 null 반환
                                .max()
                ))
                .from(bid)
                .join(bid.auction, auction)
                .join(auction.product, product)
                .join(product.seller, user)
                .join(product.category, category)
                .where(bid.bidder.userId.eq(userId))
                .groupBy(
                        product.productId,
                        user.nickname,
                        category.category,
                        product.title,
                        product.productStatus,
                        product.createdAt,
                        auction.auctionId,
                        auction.startPrice,
                        auction.currentPrice,
                        auction.startTime,
                        auction.endTime,
                        auction.status
                )

                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .orderBy(bid.createdAt.max().desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageSize) {
            content.remove(pageSize);
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
