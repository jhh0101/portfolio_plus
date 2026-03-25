package org.example.auctioncommon.domain.order.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.auctioncommon.domain.auction.entity.Auction
import org.example.auctioncommon.domain.product.entity.ProductStatus
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.global.base.BaseCreatedAt

@Entity
@Table(
    name = "orders",
    indexes = [Index(name = "idx_order_buyer", columnList = "buyer_id"), Index(
        name = "idx_order_auction",
        columnList = "auction_id"
    )]
)
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    var orderId: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    val auction: Auction,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    val buyer: User,

    @Column(name = "final_price")
    var finalPrice: Long? = 0L,

) : BaseCreatedAt() {
    val seller: User?
        get() = auction.product?.seller

    val title: String?
        get() = auction.product?.title
}
