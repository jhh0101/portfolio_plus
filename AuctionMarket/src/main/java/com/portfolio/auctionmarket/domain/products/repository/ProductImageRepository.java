package com.portfolio.auctionmarket.domain.products.repository;

import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct_ProductIdOrderByImageOrderAsc(Long productId);

    @Modifying
    @Query("UPDATE ProductImage p SET p.imageOrder = p.imageOrder + 1" +
            "WHERE p.product.productId = :productId " +
            "AND p.imageOrder >= :newOrder " +
            "AND p.imageOrder < :oldOrder")
    void shiftOrders(@Param("productId") Long productId,
                     @Param("newOrder") Integer newOrder,
                     @Param("oldOrder") Integer oldOrder);

    @Query("SELECT COALESCE(MAX(p.imageOrder), 0) FROM ProductImage p " +
            "WHERE p.product.productId = :productId")
    Integer findMaxOrderByProductId(@Param("productId") Long productId);
}
