package org.example.auctioncommon.domain.product.repository

import org.example.auctioncommon.domain.product.entity.ProductImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductImageRepository : JpaRepository<ProductImage, Long> {
    fun findByProduct_ProductIdOrderByImageOrderAsc(productId: Long): List<ProductImage>

    @Modifying
    @Query(
        ("UPDATE ProductImage p SET p.imageOrder = p.imageOrder + 1" +
                "WHERE p.product.productId = :productId " +
                "AND p.imageOrder >= :newOrder " +
                "AND p.imageOrder < :oldOrder")
    )
    fun shiftOrders(
        @Param("productId") productId: Long,
        @Param("newOrder") newOrder: Int,
        @Param("oldOrder") oldOrder: Int
    )

    @Query(
        "SELECT COALESCE(MAX(p.imageOrder), 0) FROM ProductImage p " +
                "WHERE p.product.productId = :productId"
    )
    fun findMaxOrderByProductId(@Param("productId") productId: Long): Int
}
