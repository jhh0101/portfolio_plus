package com.portfolio.auctionmarket.domain.auctions.service;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.auctions.repository.AuctionRepository;
import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import com.portfolio.auctionmarket.domain.bids.entity.BidStatus;
import com.portfolio.auctionmarket.domain.bids.repository.BidRepository;
import com.portfolio.auctionmarket.domain.orders.dto.OrderResponse;
import com.portfolio.auctionmarket.domain.orders.entity.Order;
import com.portfolio.auctionmarket.domain.orders.repository.OrderRepository;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public Optional<OrderResponse> finishAuction(Long auctionId) {
        Auction auction = auctionRepository.findByIdWithPessimisticLock(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND, "옥션을 찾을 수 없습니다."));

        User user = auction.getProduct().getSeller();

        if (auction.getStatus() != AuctionStatus.PROCEEDING) {
            return Optional.empty();
        }

        auction.changeStatus(AuctionStatus.ENDED);

        Optional<Bid> topBid = bidRepository.findTopByStatusAndAuctionOrderByBidIdDesc(BidStatus.ACTIVE, auction);

        if (topBid.isPresent()) {
            Bid winnerBid = topBid.get();
            Order order = Order.builder()
                .auction(auction)
                .buyer(winnerBid.getBidder())
                .finalPrice(winnerBid.getBidPrice())
                .build();
            orderRepository.save(order);
            user.addPoint(auction.getCurrentPrice());
            auction.getProduct().changeStatus(ProductStatus.SOLD);
            log.info("경매 낙찰 완료 - ID: {}", auctionId);
            return Optional.of(OrderResponse.from(order));
        } else {
            auction.getProduct().changeStatus(ProductStatus.FAILED);
            log.info("경매 유찰 완료 (입찰자 없음) - ID: {}", auctionId);
        }

        return Optional.empty();
    }
}
