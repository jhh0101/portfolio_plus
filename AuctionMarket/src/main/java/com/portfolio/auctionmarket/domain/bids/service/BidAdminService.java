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
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BidAdminService {
    private final BidQueryRepository bidQueryRepository;
    private final BidRepository bidRepository;

    @Transactional(readOnly = true)
    public Slice<BidHistoryResponse> findBidHistory(Long userId, Pageable pageable) {
        Slice<BidHistoryResponse> responses = bidQueryRepository.findBidHistorySlice(userId, pageable);
        return responses;
    }

    @Transactional(readOnly = true)
    public Slice<BidResponseImpl> findUserBidList(Long userId, Long auctionId, Pageable pageable) {
        Slice<BidResponse> responses = bidRepository.findAllByAuction_AuctionIdToAdmin(userId, auctionId, pageable);
        return responses.map(BidResponseImpl::from);
    }
}
