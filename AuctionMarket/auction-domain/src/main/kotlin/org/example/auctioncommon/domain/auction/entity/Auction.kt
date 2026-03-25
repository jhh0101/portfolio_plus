package org.example.auctioncommon.domain.auction.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.auctioncommon.domain.bid.entity.Bid
import org.example.auctioncommon.domain.product.entity.Product
import org.example.auctioncommon.global.error.CustomException
import org.example.auctioncommon.global.error.ErrorCode
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@SQLDelete(sql = "UPDATE auctions SET status = 'CANCELED' WHERE auction_id = ?")
@SQLRestriction("status != 'CANCELED'")
@Table(name = "auctions")
class Auction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_id")
    var auctionId: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product? = null,

    @Column(name = "start_price")
    var startPrice: Long? = 0L,

    @Column(name = "current_price")
    var currentPrice: Long? = 0L,

    @Column(name = "start_time")
    var startTime: LocalDateTime? = null,

    @Column(name = "end_time")
    var endTime: LocalDateTime? = null,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: AuctionStatus? = null,

    @OneToMany(
        mappedBy = "auction",
        cascade = [CascadeType.REMOVE],
        orphanRemoval = true
    )
    val bids: MutableList<Bid> = mutableListOf()
) {

    fun updateCurrentPrice(currentPrice: Long?) {
        this.currentPrice = currentPrice
    }

    fun updateAuction(
        startPrice: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        this.startPrice = startPrice
        this.currentPrice = startPrice
        this.startTime = startTime
        this.endTime = endTime
    }

    fun validateBiddingTime() {
        val now = LocalDateTime.now()

        if ((AuctionStatus.PROCEEDING != this.status) ||
            now.isAfter(this.endTime) ||
            now.isBefore(this.startTime)
        ) {
            throw CustomException(ErrorCode.INVALID_AUCTION_TIME)
        }
    }

    fun changeStatus(status: AuctionStatus) {
        this.status = status
    }
}
