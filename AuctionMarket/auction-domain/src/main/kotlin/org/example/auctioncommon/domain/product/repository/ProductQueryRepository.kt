package org.example.auctioncommon.domain.product.repository

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.auctioncommon.domain.auction.entity.AuctionStatus
import org.example.auctioncommon.domain.auction.entity.QAuction.auction
import org.example.auctioncommon.domain.categories.entity.QCategory.category1
import org.example.auctioncommon.domain.product.dto.ProductListCondition
import org.example.auctioncommon.domain.product.entity.Product
import org.example.auctioncommon.domain.product.entity.ProductStatus
import org.example.auctioncommon.domain.product.entity.QProduct.product
import org.example.auctioncommon.domain.user.entity.QUser.user
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.util.function.LongSupplier

@Repository
class ProductQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {

    fun productList(
        userId: Long?,
        condition: ProductListCondition,
        pageable: Pageable
    ): Page<Product> {
        val content = jpaQueryFactory
            .selectFrom(product)
            .innerJoin(product.seller, user).fetchJoin()
            .innerJoin(product.auction, auction).fetchJoin()
            .leftJoin(product.category, category1).fetchJoin()
            .where(
                isMyAuction(userId),
                titleContain(condition.title),
                pathStartWith(condition.path),
                statusFilter(userId),
                auction.status.eq(AuctionStatus.PROCEEDING),
                product.productStatus.eq(ProductStatus.ACTIVE)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(orderSpecifier(condition.sort))
            .fetch()

        val count: JPAQuery<Long?> = jpaQueryFactory
            .select(product.count())
            .from(product)
            .innerJoin(product.seller, user)
            .innerJoin(product.auction, auction)
            .leftJoin(product.category, category1)
            .where(
                isMyAuction(userId),
                titleContain(condition.title),
                pathStartWith(condition.path),
                statusFilter(userId),
                auction.status.eq(AuctionStatus.PROCEEDING),
                product.productStatus.eq(ProductStatus.ACTIVE)
            )

        return PageableExecutionUtils.getPage<Product>(content, pageable, LongSupplier { count.fetchOne() ?: 0L })
    }

    fun adminProductList(
        userId: Long?,
        condition: ProductListCondition,
        pageable: Pageable
    ): Slice<Product> {
        val pageSize = pageable.pageSize

        val content = jpaQueryFactory
            .selectFrom(product)
            .innerJoin(product.seller, user).fetchJoin()
            .innerJoin(product.auction, auction).fetchJoin()
            .leftJoin(product.category, category1).fetchJoin()
            .where(
                isMyAuction(userId),
                titleContain(condition.title),
                pathStartWith(condition.path),
                statusFilter(userId)
            )
            .offset(pageable.offset)
            .limit(pageSize.toLong() + 1)
            .orderBy(orderSpecifier(condition.sort))
            .fetch()

        var hasNext = false
        if (content.size > pageSize) {
            content.removeAt(pageSize)
            hasNext = true
        }

        return SliceImpl(content, pageable, hasNext)
    }

    private fun isMyAuction(userId: Long?): BooleanExpression? {
        return userId.let { product.seller.userId.eq(it) }
    }

    private fun titleContain(title: String?): BooleanExpression? {
        return if (!title.isNullOrBlank()) product.title.contains(title) else null
    }

    private fun pathStartWith(path: String?): BooleanExpression? {
        return if (!path.isNullOrBlank()) product.category.path.eq(path)
            .or(product.category.path.startsWith("$path/")) else null
    }

    private fun statusFilter(userId: Long?): BooleanExpression? {
        return if (userId != null) {
            product.productStatus.`in`(ProductStatus.ACTIVE, ProductStatus.SOLD, ProductStatus.FAILED)
        } else null
    }

    private fun orderSpecifier(sort: String?): OrderSpecifier<*> {
        return when (sort) {
            "endingSoon" -> product.auction.endTime.asc()
            "priceHigh" -> product.auction.currentPrice.desc()
            "priceLow" -> product.auction.currentPrice.asc()
            else -> product.productId.desc()
            }
    }
}
