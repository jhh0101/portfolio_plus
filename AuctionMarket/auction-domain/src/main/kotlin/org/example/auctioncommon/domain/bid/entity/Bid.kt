package org.example.auctioncommon.domain.bid.entity

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.example.auctioncommon.domain.auction.entity.Auction
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.global.base.BaseCreatedAt

@Entity
@Table(
    name = "bids",
    indexes = [Index(
        name = "idx_bid_auction",
        columnList = "auction_id"
    ), Index(name = "idx_bid_bidder", columnList = "bidder_id")]
)
@AttributeOverride(name = "createdAt", column = Column(name = "bid_time", updatable = false))
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    var bidId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    val auction: Auction? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id")
    val bidder: User? = null,

    @Column(name = "bid_price", nullable = false)
    var bidPrice: Long,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: BidStatus? = null,
) : BaseCreatedAt() {

    fun cancelBid() {
        this.status = BidStatus.CANCELED
    }

    fun invalidBid() {
        this.status = BidStatus.INVALID
    }
}
