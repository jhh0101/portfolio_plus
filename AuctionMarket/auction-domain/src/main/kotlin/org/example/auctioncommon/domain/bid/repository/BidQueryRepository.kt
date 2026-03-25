package org.example.auctioncommon.domain.bid.repository

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.auctioncommon.domain.auction.dto.AuctionResponse
import org.example.auctioncommon.domain.auction.entity.QAuction
import org.example.auctioncommon.domain.bid.dto.BidHistoryResponse
import org.example.auctioncommon.domain.bid.entity.BidStatus
import org.example.auctioncommon.domain.bid.entity.QBid
import org.example.auctioncommon.domain.categories.entity.QCategory
import org.example.auctioncommon.domain.product.dto.ProductAndAuctionResponse
import org.example.auctioncommon.domain.product.dto.ProductResponse
import org.example.auctioncommon.domain.product.entity.QProduct
import org.example.auctioncommon.domain.product.entity.QProductImage
import org.example.auctioncommon.domain.user.entity.QUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository

@Repository
class BidQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {
    private val bid = QBid.bid
    private val auction = QAuction.auction
    private val product = QProduct.product
    private val user = QUser.user
    private val category = QCategory.category1
    private val productImage = QProductImage.productImage

    fun findBidHistoryPage(userId: Long?, pageable: Pageable): Page<BidHistoryResponse> {
        val content = jpaQueryFactory
            .select(
                Projections.constructor(
                    BidHistoryResponse::class.java,
                    Projections.constructor(
                        ProductAndAuctionResponse::class.java,
                        Projections.constructor(
                            ProductResponse::class.java,
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
                        Projections.constructor(
                            AuctionResponse::class.java,
                            auction.auctionId,
                            auction.startPrice,
                            auction.currentPrice,
                            auction.startTime,
                            auction.endTime,
                            auction.status
                        )
                    ),
                    bid.bidPrice.max() // 내 최고 입찰가
                )
            )
            .from(bid)
            .join(bid.auction, auction)
            .join(auction.product, product)
            .join(product.seller, user)
            .join(product.category, category)
            .where(
                bid.bidder.userId.eq(userId),
                bid.status.eq(BidStatus.ACTIVE)
            )
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

            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(bid.createdAt.max().desc())
            .fetch()

        val total = jpaQueryFactory
            .select(auction.countDistinct())
            .from(bid)
            .join(bid.auction, auction)
            .where(
                bid.bidder.userId.eq(userId),
                bid.status.eq(BidStatus.ACTIVE)
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    fun findBidHistorySlice(userId: Long?, pageable: Pageable): Slice<BidHistoryResponse> {
        val pageSize = pageable.pageSize

        val content = jpaQueryFactory
            .select(
                Projections.constructor(
                    BidHistoryResponse::class.java,
                    Projections.constructor(
                        ProductAndAuctionResponse::class.java,
                        Projections.constructor(
                            ProductResponse::class.java,
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
                        Projections.constructor(
                            AuctionResponse::class.java,
                            auction.auctionId,
                            auction.startPrice,
                            auction.currentPrice,
                            auction.startTime,
                            auction.endTime,
                            auction.status
                        )
                    ),
                    CaseBuilder()
                        .`when`(bid.status.eq(BidStatus.ACTIVE))
                        .then(bid.bidPrice)
                        .otherwise(0L) // 활성 입찰이 하나도 없으면 null 반환
                        .max()
                )
            )
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

            .offset(pageable.offset)
            .limit(pageSize.toLong() + 1)
            .orderBy(bid.createdAt.max().desc())
            .fetch()

        var hasNext = false
        if (content.size > pageSize) {
            content.removeAt(pageSize)
            hasNext = true
        }

        return SliceImpl(content, pageable, hasNext)
    }
}
