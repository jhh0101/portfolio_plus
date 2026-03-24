package com.portfolio.auctionmarket.domain.ratings.repository;

import com.portfolio.auctionmarket.domain.orders.entity.Order;
import com.portfolio.auctionmarket.domain.ratings.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByOrder(Order order);

    @EntityGraph(attributePaths = {"toUser", "fromUser", "order"})
    Page<Rating> findAllByToUser_UserId(Long toUserUserId, Pageable pageable);

    Rating findByOrder_OrderId(Long orderId);
}
