package com.portfolio.auctionmarket.domain.bids.entity;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.global.base.BaseCreatedAt;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bids", indexes = {
        @Index(name = "idx_bid_auction", columnList = "auction_id"),
        @Index(name = "idx_bid_bidder", columnList = "bidder_id")
})
@AttributeOverride(name = "createdAt", column = @Column(name = "bid_time",updatable = false))
public class Bid extends BaseCreatedAt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private Long bidId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id")
    private User bidder;

    @Column(name = "bid_price", nullable = false)
    private Long bidPrice;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BidStatus status;

    public void cancelBid() {
        this.status = BidStatus.CANCELED;
    }

    public void invalidBid() {
        this.status = BidStatus.INVALID;
    }
}
