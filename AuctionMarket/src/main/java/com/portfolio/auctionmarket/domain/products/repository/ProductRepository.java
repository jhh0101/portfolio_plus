package com.portfolio.auctionmarket.domain.products.repository;

import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import org.springframework.data.jpa.repository.QueryHints;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByCategory_CategoryId(Long categoryId);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.auction a " +
            "WHERE p.productId = :productId")
    Optional<Product> findWithAuctionById(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.productId = :productId")
    Integer viewCount(@Param("productId") Long productId);

    @Query("SELECT COUNT(p) " +
            "FROM Product p " +
            "JOIN p.auction a " +
            "WHERE a.status = 'PROCEEDING' " +
            "AND p.seller.userId = :sellerId")
    Long productCount(@Param("sellerId") Long sellerId);

    List<Product> findAllBySeller_UserId(Long userId);

    void deleteAllBySeller_UserId(Long userId);

    @QueryHints(value = {
            @QueryHint(name = "org.hibernate.fetchSize", value = "100"),
            @QueryHint(name = "org.hibernate.readOnly", value = "true")
    })
    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.auction a " +
            "WHERE p.productStatus = :status")
    Stream<Product> findAllByProductStatus(@Param("status") ProductStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE Product p SET p.productStatus = :status " +
            "WHERE p.productStatus = :activeStatus " +
            "AND p.auction.status = :auctionStatus AND p.auction.endTime <= :now " +
            "AND EXISTS (SELECT 1 FROM Bid b WHERE b.auction.auctionId = p.auction.auctionId)")
    void updateProductStatusSold(@Param("now") LocalDateTime now,
                                 @Param("status") ProductStatus status,
                                 @Param("activeStatus") ProductStatus activeStatus,
                                 @Param("auctionStatus") AuctionStatus auctionStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE Product p SET p.productStatus = :status " +
            "WHERE p.productStatus = :activeStatus " +
            "AND p.auction.status = :auctionStatus AND p.auction.endTime <= :now " +
            "AND NOT EXISTS (SELECT 1 FROM Bid b WHERE b.auction.auctionId = p.auction.auctionId)")
    void updateProductStatusFailed(@Param("now") LocalDateTime now,
                                 @Param("status") ProductStatus status,
                                 @Param("activeStatus") ProductStatus activeStatus,
                                 @Param("auctionStatus") AuctionStatus auctionStatus);
}
