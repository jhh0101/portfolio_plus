package org.example.auctioncommon.domain.bid.repository

import org.example.auctioncommon.domain.auction.entity.Auction
import org.example.auctioncommon.domain.bid.dto.BidInfo
import org.example.auctioncommon.domain.bid.entity.Bid
import org.example.auctioncommon.domain.bid.entity.BidStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BidRepository : JpaRepository<Bid, Long> {
    fun existsByStatusAndAuction(status: BidStatus, auction: Auction): Boolean

    fun findTopByStatusAndAuctionOrderByBidIdDesc(status: BidStatus, auction: Auction): Bid?

    fun findAllByStatusAndAuctionOrderByBidPriceDesc(
        status: BidStatus,
        auction: Auction
    ): List<Bid>

    @Query(
        ("SELECT b " +
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
    )
    fun findTopBidsPerAuctionBySellerId(@Param("sellerId") sellerId: Long): List<Bid>

    @Query(
        ("SELECT b FROM Bid b " +
                "WHERE b.bidder.userId = :userId " +
                "AND b.bidId IN (" +
                "   SELECT MAX(b2.bidId) FROM Bid b2 " +
                "   WHERE b2.bidder.userId = :userId " +
                "   AND b2.status = 'ACTIVE' " +
                "   GROUP BY b2.auction" +
                ") " +
                "AND b.status = 'ACTIVE'")
    )
    fun findLatestBidsByUserId(@Param("userId") userId: kotlin.Long?): List<Bid>

    fun findTopByStatusAndAuctionOrderByBidPriceDesc(status: BidStatus, auction: Auction): Bid?

    @Query(
        ("SELECT COUNT(b) " +
                "FROM Bid b " +
                "JOIN b.auction a " +
                "WHERE a.currentPrice = b.bidPrice " +
                "AND a.status = 'PROCEEDING' " +
                "AND b.bidder.userId = :bidderId " +
                "AND b.status = 'ACTIVE'")
    )
    fun bidCount(@Param("bidderId") bidderId: Long): Long

    @Query(
        value = ("SELECT b.bidId AS bidId, b.auction.auctionId AS auctionId, u.nickname AS nickname, b.bidPrice AS bidPrice, b.createdAt AS createdAt, b.status AS status " +
                "FROM Bid b " +
                "LEFT JOIN User u ON b.bidder.userId = u.userId " +
                "WHERE b.auction.auctionId = :auctionId " +
                "AND b.status = 'ACTIVE'"),
        countQuery = "SELECT COUNT(*) FROM Bid b WHERE b.auction.auctionId = :auctionId AND b.status = 'ACTIVE'"
    )
    fun findAllByAuction_AuctionId(
        @Param("auctionId") auctionId: Long, pageable: Pageable): Page<BidInfo>

    @Query(
        value = ("SELECT b.bid_id AS bidId, b.auction_id AS auctionId, u.nickname AS nickname, b.bid_price AS bidPrice, b.bid_time AS bidTime, b.status AS status " +
                "FROM bids b " +
                "LEFT JOIN users u ON b.bidder_id = u.user_id " +
                "WHERE b.auction_id = :auctionId " +
                "AND b.bidder_id = :userId"), nativeQuery = true
    )
    fun findAllByAuction_AuctionIdToAdmin(
        @Param("userId") userId: Long,
        @Param("auctionId") auctionId: Long,
        pageable: Pageable
    ): Slice<BidInfo>
}
