package org.example.auctioncommon.domain.bid.service

import org.example.auctioncommon.domain.auction.entity.Auction
import org.example.auctioncommon.domain.auction.error.AuctionErrorCode
import org.example.auctioncommon.domain.auction.repository.AuctionRepository
import org.example.auctioncommon.domain.bid.dto.*
import org.example.auctioncommon.domain.bid.entity.Bid
import org.example.auctioncommon.domain.bid.entity.BidStatus
import org.example.auctioncommon.domain.bid.error.BidErrorCode
import org.example.auctioncommon.domain.bid.repository.BidQueryRepository
import org.example.auctioncommon.domain.bid.repository.BidRepository
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.domain.user.error.UserErrorCode
import org.example.auctioncommon.domain.user.repository.UserRepository
import org.example.auctioncommon.global.error.CustomException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BidService(
    private val bidRepository: BidRepository,
    private val bidQueryRepository: BidQueryRepository,
    private val userRepository: UserRepository,
    private val auctionRepository: AuctionRepository,
    private val bidProcessor: BidProcessor,
) {

    @Transactional
    fun addBid(userId: Long, auctionId: Long, request: BidRequest): BidResultResponse {
        val user: User = userRepository.findByIdWithPessimisticLock(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        val auction: Auction = auctionRepository.findByIdWithPessimisticLock(auctionId)
            ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND)

        val lastBid: Bid? = bidRepository.findTopByStatusAndAuctionOrderByBidIdDesc(BidStatus.ACTIVE, auction)

        val bid = bidProcessor.addBid(user, auction, request.bidPrice, lastBid)

        val bidSave: Bid = bidRepository.save(bid)

        return bidSave.toDto()
    }

    @Transactional(readOnly = true)
    fun findBid(auctionId: Long, pageable: Pageable): Page<BidInfoImpl> {
        return bidRepository.findAllByAuction_AuctionId(auctionId, pageable).map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun findBidHistory(userId: Long, pageable: Pageable): Page<BidHistoryResponse> {
        return bidQueryRepository.findBidHistoryPage(userId, pageable)
    }

    @Transactional
    fun cancelBid(userId: Long, bidId: Long, auctionId: Long): BidResultResponse {
        val auction: Auction = auctionRepository.findByIdWithPessimisticLock(auctionId)
            ?: throw CustomException(AuctionErrorCode.AUCTION_NOT_FOUND)

        val bid: Bid = bidRepository.findByIdOrNull(bidId)
            ?: throw CustomException(BidErrorCode.BID_NOT_FOUND)

        val currentTopBid: Bid =
            bidRepository.findTopByStatusAndAuctionOrderByBidPriceDesc(BidStatus.ACTIVE, auction)
                ?: throw CustomException(
            BidErrorCode.BID_NOT_FOUND, "활성화된 입찰 내역이 없습니다.")

        val bidList: List<Bid> =
            bidRepository.findAllByStatusAndAuctionOrderByBidPriceDesc(BidStatus.ACTIVE, auction)

        bidProcessor.cancelBid(auction, userId, bid, currentTopBid, bidList)

        return bid.toDto()
    }
}
