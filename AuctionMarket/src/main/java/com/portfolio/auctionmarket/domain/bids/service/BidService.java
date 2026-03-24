package com.portfolio.auctionmarket.domain.bids.service;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.auctions.repository.AuctionRepository;
import com.portfolio.auctionmarket.domain.bids.dto.*;
import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import com.portfolio.auctionmarket.domain.bids.entity.BidStatus;
import com.portfolio.auctionmarket.domain.bids.repository.BidQueryRepository;
import com.portfolio.auctionmarket.domain.bids.repository.BidRepository;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final BidQueryRepository bidQueryRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    @Transactional
    public BidResultResponse addBid(Long userId, Long auctionId, BidRequest request) {
        User user = userRepository.findByIdWithPessimisticLock(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자 포인트 체크
        if (user.getPoint() < request.getBidPrice()) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINTS, "소지중인 포인트가 부족합니다.");
        }

        Auction auction = auctionRepository.findByIdWithPessimisticLock(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        auction.validateBiddingTime();

        // 본인 경매 입찰 금지
        if (auction.getProduct().getSeller().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.SELF_BID_NOT_ALLOWED, "본인 상품은 입찰할 수 없습니다.");
        }

        // 입찰가 체크
        Optional<Bid> lastBid = bidRepository.findTopByStatusAndAuctionOrderByBidIdDesc(BidStatus.ACTIVE, auction);

        if (lastBid.isEmpty()) {
            if (auction.getStartPrice() > request.getBidPrice()) {
                throw new CustomException(ErrorCode.BID_PRICE_TOO_LOW, "시작가보다 높아야 합니다.");
            }
        } else {
            Bid lastBidOpt = lastBid.get();
            if (lastBidOpt.getBidder().getUserId().equals(userId)) {
                throw new CustomException(ErrorCode.ALREADY_HIGHEST_BIDDER, "이미 입찰중입니다.");
            }

            if (auction.getCurrentPrice() >= request.getBidPrice()) {
                throw new CustomException(ErrorCode.BID_PRICE_TOO_LOW, "현재 입찰가보다 높아야 합니다.");
            }
        }

        bidRepository.findTopByStatusAndAuctionOrderByBidIdDesc(BidStatus.ACTIVE, auction)
                .ifPresent(lastBidder -> {
                    User bidder = lastBidder.getBidder();
                    bidder.addPoint(lastBidder.getBidPrice());
                });

        user.subPoint(request.getBidPrice());

        Bid bid = Bid.builder()
                .auction(auction)
                .bidder(user)
                .bidPrice(request.getBidPrice())
                .status(BidStatus.ACTIVE)
                .build();

        Bid bidSave = bidRepository.save(bid);

        auction.updateCurrentPrice(request.getBidPrice());

        return BidResultResponse.from(bidSave);
    }

    @Transactional(readOnly = true)
    public Page<BidResponseImpl> findBid(Long auctionId, Pageable pageable) {
        Page<BidResponse> bids = bidRepository.findAllByAuction_AuctionId(auctionId, pageable);
        return bids.map(BidResponseImpl::from);
    }

    // 입찰 취소(비즈니스 관점에선 필요 없음)
    @Transactional
    public BidResultResponse cancelBid(Long userId, Long bidId, Long auctionId) {
        Auction auction = auctionRepository.findByIdWithPessimisticLock(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        if (auction.getStatus() != AuctionStatus.PROCEEDING) {
            return null;
        }

        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND));

        if (!userId.equals(bid.getBidder().getUserId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "사용자 정보가 일치하지 않습니다.");
        }

        if (!bid.getAuction().getAuctionId().equals(auctionId)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "경매 정보가 올바르지 않습니다.");
        }

        Bid currentTopBid = bidRepository.findTopByStatusAndAuctionOrderByBidPriceDesc(BidStatus.ACTIVE, bid.getAuction())
                .orElseThrow(() -> new CustomException(ErrorCode.BID_NOT_FOUND, "활성화된 입찰 내역이 없습니다."));

        if (!currentTopBid.getBidder().getUserId().equals(userId)){
            throw new CustomException(ErrorCode.BID_NOT_FOUND);
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(bid.getCreatedAt().plusMinutes(10)) || now.isAfter(auction.getEndTime().minusMinutes(10))) {
            throw new CustomException(ErrorCode.BID_CANCEL_RESTRICTED);
        }

        // 포인트 환불
        bid.getBidder().addPoint(bid.getBidPrice());

        // 입찰 취소
        bid.cancelBid();

        // 입찰자 리스트
        List<Bid> bidList = bidRepository.findAllByStatusAndAuctionOrderByBidPriceDesc(BidStatus.ACTIVE, bid.getAuction());

        boolean bidFound = false;

        // 입찰자 포인트 검사 & 최고입찰자 데이터 삽입
        for (Bid lastBidder : bidList) {
            User bidder = lastBidder.getBidder();
            if (bidder.getUserId().equals(userId)) {
                continue;
            }
            if (bidder.getPoint() >= lastBidder.getBidPrice()) {
                bid.getAuction().updateCurrentPrice(lastBidder.getBidPrice());
                bidder.subPoint(lastBidder.getBidPrice());
                bidFound = true;
                break;
            } else {
                lastBidder.invalidBid();
            }
        }
        if (!bidFound) {
            bid.getAuction().updateCurrentPrice(bid.getAuction().getStartPrice());
        }

        return BidResultResponse.from(bid);
    }

    @Transactional(readOnly = true)
    public Page<BidHistoryResponse> findBidHistory(Long userId, Pageable pageable) {
        Page<BidHistoryResponse> responses = bidQueryRepository.findBidHistoryPage(userId, pageable);
        return responses;
    }

}
