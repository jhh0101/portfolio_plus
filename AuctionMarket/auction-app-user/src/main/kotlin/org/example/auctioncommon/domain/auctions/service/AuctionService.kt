package org.example.auctioncommon.domain.auctions.service

import org.example.auctioncommon.domain.auction.entity.Auction
import org.example.auctioncommon.domain.auction.entity.AuctionStatus
import org.example.auctioncommon.domain.auction.error.AuctionErrorCode
import org.example.auctioncommon.domain.auction.repository.AuctionRepository
import org.example.auctioncommon.domain.auction.service.AuctionProcessor
import org.example.auctioncommon.domain.bid.entity.Bid
import org.example.auctioncommon.domain.bid.entity.BidStatus
import org.example.auctioncommon.domain.bid.repository.BidRepository
import org.example.auctioncommon.domain.order.entity.Order
import org.example.auctioncommon.domain.order.repository.OrderRepository
import org.example.auctioncommon.domain.orders.dto.OrderResponse
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.global.error.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AuctionService(
    private val auctionRepository: AuctionRepository,
    private val auctionProcessor: AuctionProcessor,
    private val bidRepository: BidRepository,
    private val orderRepository: OrderRepository,
) {


    @Transactional
    fun finishAuction(auctionId: Long): Optional<OrderResponse> {
        val auction: Auction = auctionRepository.findByIdWithPessimisticLock(auctionId)
            ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND, "옥션을 찾을 수 없습니다.")

        val user: User? = auction.product?.seller

        auction.changeStatus(AuctionStatus.ENDED)

        val topBid: Optional<Bid> = Optional.of(bidRepository.findTopByStatusAndAuctionOrderByBidIdDesc(BidStatus.ACTIVE, auction) as Bid)

        val order: Order? = auctionProcessor.validateFinishAuction(auction, topBid, user)

        orderRepository.save(order)

        return Optional.empty()
    }
}
