package com.portfolio.auctionmarket.domain.orders.repository;

import com.portfolio.auctionmarket.domain.orders.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"auction", "auction.product", "auction.product.image"})
    Page<Order> findAllByBuyer_UserId(Long userId, Pageable pageable);

    Optional<Order> findByAuction_AuctionId(Long auctionAuctionId);
}
