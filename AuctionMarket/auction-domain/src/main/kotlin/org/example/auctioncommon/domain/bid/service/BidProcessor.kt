package org.example.auctioncommon.domain.bid.service

import org.example.auctioncommon.domain.auction.entity.Auction
import org.example.auctioncommon.domain.auction.entity.AuctionStatus
import org.example.auctioncommon.domain.auction.error.AuctionErrorCode
import org.example.auctioncommon.domain.bid.entity.Bid
import org.example.auctioncommon.domain.bid.entity.BidStatus
import org.example.auctioncommon.domain.bid.error.BidErrorCode
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.domain.user.error.UserErrorCode
import org.example.auctioncommon.global.error.CustomException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BidProcessor {

    fun addBid(user: User, auction: Auction, bidPrice: Long, lastBid: Bid?): Bid {
        // 1. 비즈니스 검증 (포인트 확인, 본인 여부 확인, 입찰가 확인 등)
        if (user.point < bidPrice) throw CustomException(BidErrorCode.NOT_ENOUGH_POINTS)

        auction.validateBiddingTime()

        if (auction.product?.seller?.userId == user.userId) throw CustomException(BidErrorCode.SELF_BID_NOT_ALLOWED)

        if (lastBid == null) {
            val startPrice = auction.startPrice ?: 0L
            if (startPrice > bidPrice) throw CustomException(BidErrorCode.BID_PRICE_TOO_LOW);

        } else {
            if (lastBid.bidder?.userId == user.userId) throw CustomException(BidErrorCode.ALREADY_HIGHEST_BIDDER)

            val currentPrice = auction.currentPrice ?: 0L
            if (currentPrice >= bidPrice) throw CustomException(BidErrorCode.BID_PRICE_TOO_LOW);

            lastBid.bidder?.addPoint(bidPrice)
        }

        user.subPoint(bidPrice)
        auction.updateCurrentPrice(bidPrice)

        return Bid(auction = auction, bidder = user, bidPrice = bidPrice, status = BidStatus.ACTIVE)
    }

    fun cancelBid(auction: Auction, userId: Long, bid: Bid, currentTopBid: Bid, bidList: List<Bid>) {
        if (auction.status != AuctionStatus.PROCEEDING) {
            throw CustomException(AuctionErrorCode.AUCTION_ENDED)
        }

        if (userId != bid.bidder?.userId) {
            throw CustomException(UserErrorCode.USER_VERIFICATION_FAILED, "사용자 정보가 일치하지 않습니다.")
        }

        if (bid.auction?.auctionId != auction.auctionId) {
            throw CustomException(AuctionErrorCode.INVALID_AUCTION_INFO, "경매 정보가 올바르지 않습니다.")
        }

        if (currentTopBid.bidder?.userId != userId) {
            throw CustomException(BidErrorCode.BID_NOT_FOUND)
        }

        val now = LocalDateTime.now()

        if (now.isAfter(bid.createdAt?.plusMinutes(10)) || now.isAfter(auction.endTime?.minusMinutes(10))) {
            throw CustomException(BidErrorCode.BID_CANCEL_RESTRICTED)
        }

        bid.bidder.addPoint(bid.bidPrice);

        bid.cancelBid();

        var bidFound: Boolean = false

        for (lastBidder in bidList) {
            val bidder: User? = lastBidder.bidder
            if (bidder?.userId == userId) {
                continue;
            }
            val bidderPoint = bidder?.point ?: 0L
            if (bidderPoint >= lastBidder.bidPrice) {
                bid.auction?.updateCurrentPrice(lastBidder.bidPrice)
                bidder?.subPoint(lastBidder.bidPrice)
                bidFound = true
                break
            } else {
                lastBidder.invalidBid()
            }
        }

        if (!bidFound) {
            bid.auction?.updateCurrentPrice(bid.auction.startPrice)
        }
    }
}
