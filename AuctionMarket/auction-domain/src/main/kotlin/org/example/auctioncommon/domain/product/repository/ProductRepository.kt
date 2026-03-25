package org.example.auctioncommon.domain.product.repository

import jakarta.persistence.QueryHint
import org.example.auctioncommon.domain.auction.entity.AuctionStatus
import org.example.auctioncommon.domain.product.entity.Product
import org.example.auctioncommon.domain.product.entity.ProductStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.stream.Stream

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByCategory_CategoryId(categoryId: Long?): Product

    @Query(
        ("SELECT p FROM Product p " +
                "JOIN FETCH p.auction a " +
                "WHERE p.productId = :productId")
    )
    fun findWithAuctionById(@Param("productId") productId: Long): Product?

    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.productId = :productId")
    fun viewCount(@Param("productId") productId: Long): Int

    @Query(
        ("SELECT COUNT(p) " +
                "FROM Product p " +
                "JOIN p.auction a " +
                "WHERE a.status = 'PROCEEDING' " +
                "AND p.seller.userId = :sellerId")
    )
    fun productCount(@Param("sellerId") sellerId: Long): Long

    fun findAllBySeller_UserId(userId: Long): List<Product>

    fun deleteAllBySeller_UserId(userId: Long)

    @QueryHints(value = [
            QueryHint(name = "org.hibernate.fetchSize", value = "100"),
            QueryHint(name = "org.hibernate.readOnly", value = "true")
    ])
    @Query(
        ("SELECT p FROM Product p " +
                "JOIN FETCH p.auction a " +
                "WHERE p.productStatus = :status")
    )
    fun findAllByProductStatus(@Param("status") status: ProductStatus): Stream<Product>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(
        ("UPDATE Product p SET p.productStatus = :status " +
                "WHERE p.productStatus = :activeStatus " +
                "AND p.auction.status = :auctionStatus AND p.auction.endTime <= :now " +
                "AND EXISTS (SELECT 1 FROM Bid b WHERE b.auction.auctionId = p.auction.auctionId)")
    )
    fun updateProductStatusSold(
        @Param("now") now: LocalDateTime,
        @Param("status") status: ProductStatus,
        @Param("activeStatus") activeStatus: ProductStatus,
        @Param("auctionStatus") auctionStatus: AuctionStatus
    )

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(
        ("UPDATE Product p SET p.productStatus = :status " +
                "WHERE p.productStatus = :activeStatus " +
                "AND p.auction.status = :auctionStatus AND p.auction.endTime <= :now " +
                "AND NOT EXISTS (SELECT 1 FROM Bid b WHERE b.auction.auctionId = p.auction.auctionId)")
    )
    fun updateProductStatusFailed(
        @Param("now") now: LocalDateTime,
        @Param("status") status: ProductStatus,
        @Param("activeStatus") activeStatus: ProductStatus,
        @Param("auctionStatus") auctionStatus: AuctionStatus
    )
}
