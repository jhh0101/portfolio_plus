package com.portfolio.auctionmarket.domain.auctions.entity;

import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@SQLDelete(sql = "UPDATE auctions SET status = 'CANCELED' WHERE auction_id = ?")
@SQLRestriction("status != 'CANCELED'")
@Table(name = "auctions")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_id")
    private Long auctionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "start_price")
    private Long startPrice;

    @Column(name = "current_price")
    private Long currentPrice;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "auction", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();

    public void updateCurrentPrice(Long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void updateAuction(Long startPrice, LocalDateTime startTime, LocalDateTime endTime) {
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void validateBiddingTime() {
        LocalDateTime now = LocalDateTime.now();

        if (!AuctionStatus.PROCEEDING.equals(this.status) ||
                now.isAfter(this.endTime) ||
                now.isBefore(this.startTime)) {
            throw new CustomException(ErrorCode.INVALID_AUCTION_TIME);
        }
    }

    public void changeStatus(AuctionStatus status) {
        this.status = status;
    }

}
