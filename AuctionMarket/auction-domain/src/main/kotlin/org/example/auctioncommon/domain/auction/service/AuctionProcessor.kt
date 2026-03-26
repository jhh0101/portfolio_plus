package org.example.auctioncommon.domain.auction.service

import org.example.auctioncommon.domain.auction.entity.Auction
import org.example.auctioncommon.domain.auction.entity.AuctionStatus
import org.example.auctioncommon.domain.bid.entity.Bid
import org.example.auctioncommon.domain.order.entity.Order
import org.example.auctioncommon.domain.product.entity.ProductStatus
import org.example.auctioncommon.domain.user.entity.User
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class AuctionProcessor {
    private val log = LoggerFactory.getLogger(javaClass)

    fun validateFinishAuction(auction: Auction, topBid: Optional<Bid>, user: User?) : Order? {
        if (auction.status != AuctionStatus.PROCEEDING) {
            return null
        }
        auction.changeStatus(AuctionStatus.ENDED)

        if (topBid.isPresent) {
            val winnerBid: Bid = topBid.get()
            val order: Order = Order(
                auction = auction,
                buyer = winnerBid.bidder,
                finalPrice = winnerBid.bidPrice
            )
            user?.addPoint(auction.currentPrice)
            auction.product?.changeStatus(ProductStatus.SOLD)
            log.info("경매 낙찰 완료 - ID: {}", auction.auctionId)
            return order
        } else {
            auction.product?.changeStatus(ProductStatus.FAILED)
            log.info("경매 유찰 완료 (입찰자 없음) - ID: {}", auction.auctionId)
        }
        return null
    }
}





