package com.portfolio.auctionmarket.domain.bids.repository;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.bids.dto.BidResponse;
import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import com.portfolio.auctionmarket.domain.bids.entity.BidStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Boolean existsByStatusAndAuction(BidStatus status, Auction auction);

    Optional<Bid> findTopByStatusAndAuctionOrderByBidIdDesc(BidStatus status, Auction auction);

    List<Bid> findAllByStatusAndAuctionOrderByBidPriceDesc(BidStatus status, Auction auction);

    @Query("SELECT b " +
            "FROM Bid b " +
            "JOIN FETCH b.bidder " +
            "JOIN b.auction a " +
            "JOIN a.product p " +
            "JOIN p.seller s " +
            "WHERE s.userId = :sellerId " +
            "AND b.bidPrice = (" +
            "SELECT MAX(b2.bidPrice) " +
            "FROM Bid b2 " +
            "WHERE b2.auction = b.auction " +
            "AND b2.status = 'ACTIVE') " +
            "AND a.status = 'PROCEEDING' " +
            "AND b.status = 'ACTIVE'")
    List<Bid> findTopBidsPerAuctionBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT b FROM Bid b " +
            "WHERE b.bidder.userId = :userId " +
            "AND b.bidId IN (" +
            "   SELECT MAX(b2.bidId) FROM Bid b2 " +
            "   WHERE b2.bidder.userId = :userId " +
            "   AND b2.status = 'ACTIVE' " +
            "   GROUP BY b2.auction" +
            ") " +
            "AND b.status = 'ACTIVE'")
    List<Bid> findLatestBidsByUserId(@Param("userId") Long userId);

    Optional<Bid> findTopByStatusAndAuctionOrderByBidPriceDesc(BidStatus status, Auction auction);

    @Query("SELECT COUNT(b) " +
            "FROM Bid b " +
            "JOIN b.auction a " +
            "WHERE a.currentPrice = b.bidPrice " +
            "AND a.status = 'PROCEEDING' " +
            "AND b.bidder.userId = :bidderId " +
            "AND b.status = 'ACTIVE'")
    Long bidCount(@Param("bidderId") Long bidderId);

    @Query(value = "SELECT b.bidId AS bidId, b.auction.auctionId AS auctionId, u.nickname AS nickname, b.bidPrice AS bidPrice, b.createdAt AS createdAt, b.status AS status " +
            "FROM Bid b " +
            "LEFT JOIN User u ON b.bidder.userId = u.userId " +
            "WHERE b.auction.auctionId = :auctionId " +
            "AND b.status = 'ACTIVE'",

            countQuery = "SELECT COUNT(*) FROM Bid b WHERE b.auction.auctionId = :auctionId AND b.status = 'ACTIVE'")
    Page<BidResponse> findAllByAuction_AuctionId(@Param("auctionId") Long auctionId, Pageable pageable);

    @Query(value = "SELECT b.bid_id AS bidId, b.auction_id AS auctionId, u.nickname AS nickname, b.bid_price AS bidPrice, b.bid_time AS bidTime, b.status AS status " +
            "FROM bids b " +
            "LEFT JOIN users u ON b.bidder_id = u.user_id " +
            "WHERE b.auction_id = :auctionId " +
            "AND b.bidder_id = :userId",
            nativeQuery = true)
    Slice<BidResponse> findAllByAuction_AuctionIdToAdmin(@Param("userId") Long userId, @Param("auctionId") Long auctionId, Pageable pageable);
}
