package com.portfolio.auctionmarket.domain.orders.entity;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.global.base.BaseCreatedAt;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_buyer", columnList = "buyer_id"),
        @Index(name = "idx_order_auction", columnList = "auction_id")
})
@Builder
public class Order extends BaseCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @Column(name = "final_price")
    private Long finalPrice;

    public User getSeller() {
        return auction.getProduct().getSeller();
    }

    public String getTitle() {
        return auction.getProduct().getTitle();
    }

}
