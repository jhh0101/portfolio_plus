package com.portfolio.auctionmarket.bid.service;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.auctions.repository.AuctionRepository;
import com.portfolio.auctionmarket.domain.bids.dto.BidRequest;
import com.portfolio.auctionmarket.domain.bids.dto.BidResultResponse;
import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import com.portfolio.auctionmarket.domain.bids.entity.BidStatus;
import com.portfolio.auctionmarket.domain.bids.repository.BidQueryRepository;
import com.portfolio.auctionmarket.domain.bids.repository.BidRepository;
import com.portfolio.auctionmarket.domain.bids.service.BidService;
import com.portfolio.auctionmarket.domain.categories.entity.Category;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.entity.UserStatus;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BiddingServiceTest {
    @Mock
    private BidRepository bidRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuctionRepository auctionRepository;

    @InjectMocks
    private BidService bidService;

    private User testUser1;
    private User testUser2;
    private Product testProduct;
    private Auction testAuction;
    private Category testCategory;

    @BeforeEach
    void setUp() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        testCategory = Category.builder()
                .category("testCategory")
                .parent(null)
                .build();

        testUser1 = User.builder()
                .userId(1L)
                .email("test1@test.com")
                .password("test!@#")
                .username("test1")
                .nickname("test1")
                .phone("01011111111")
                .role(Role.SELLER)
                .baseAddress("123")
                .detailAddress("456")
                .point(1000000L)
                .avgRating(0.0)
                .status(UserStatus.NORMAL)
                .build();

        testUser2 = User.builder()
                .userId(2L)
                .email("test2@test.com")
                .password("test!@#")
                .username("test2")
                .nickname("test2")
                .phone("01022222222")
                .role(Role.USER)
                .baseAddress("123")
                .detailAddress("456")
                .point(1000000L)
                .avgRating(0.0)
                .status(UserStatus.NORMAL)
                .build();

        testProduct = Product.builder()
                .productId(1L)
                .seller(testUser1)
                .category(testCategory)
                .title("testProduct")
                .description("testProductDesc")
                .viewCount(0)
                .productStatus(ProductStatus.ACTIVE)
                .image(new ArrayList<>())
                .build();

        testAuction = Auction.builder()
                .auctionId(1L)
                .product(testProduct)
                .startPrice(10000L)
                .currentPrice(10000L)
                .startTime(now)
                .endTime(now.plusDays(5))
                .status(AuctionStatus.PROCEEDING)
                .build();
    }

    // 입찰 테스트
    @Test
    void bidding() {
        // given (준비)
        BidRequest request = new BidRequest(15000L);
        given(userRepository.findByIdWithPessimisticLock(testUser2.getUserId()))
                .willReturn(Optional.of(testUser2));

        given(auctionRepository.findByIdWithPessimisticLock(testAuction.getAuctionId()))
                .willReturn(Optional.of(testAuction));

        given(bidRepository.findTopByStatusAndAuctionOrderByBidIdDesc(BidStatus.ACTIVE, testAuction))
                .willReturn(Optional.empty());

        Bid savedBid = Bid.builder()
                .bidId(1L)
                .auction(testAuction)
                .bidder(testUser2)
                .bidPrice(request.getBidPrice())
                .status(BidStatus.ACTIVE)
                .build();

        given(bidRepository.save(any(Bid.class))).willReturn(savedBid);

        // when & then (실행 및 검증)
        BidResultResponse response = bidService.addBid(testUser2.getUserId(), testAuction.getAuctionId(), request);

        assertThat(response.getPrice()).isEqualTo(15000L);
        assertThat(testUser2.getPoint()).isEqualTo(985000L);
        assertThat(testAuction.getCurrentPrice()).isEqualTo(15000L);
    }

    @Test
    void bidding_userNotFound() {
        // given (준비)
        BidRequest request = new BidRequest(15000L);
        given(userRepository.findByIdWithPessimisticLock(testUser2.getUserId()))
                .willReturn(Optional.empty());

        // when & then (실행 및 검증)
        CustomException exception = assertThrows(CustomException.class,
                () -> bidService.addBid(testUser2.getUserId(), testAuction.getAuctionId(), request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
        assertThat(exception.getErrorCode().getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void bidding_notEnoughPoints() {
        // given (준비)
        BidRequest request = new BidRequest(15000L);
        testUser2.subPoint(1000000L);

        given(userRepository.findByIdWithPessimisticLock(testUser2.getUserId()))
                .willReturn(Optional.of(testUser2));

        // when & then (실행 및 검증)
        CustomException exception = assertThrows(CustomException.class,
                () -> bidService.addBid(testUser2.getUserId(), testAuction.getAuctionId(), request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.NOT_ENOUGH_POINTS.getCode());
        assertThat(exception.getErrorCode().getMessage()).isEqualTo(ErrorCode.NOT_ENOUGH_POINTS.getMessage());
    }

    @Test
    void bidding_auctionNotFound() {
        // given (준비)
        BidRequest request = new BidRequest(15000L);
        given(userRepository.findByIdWithPessimisticLock(testUser2.getUserId()))
                .willReturn(Optional.of(testUser2));

        given(auctionRepository.findByIdWithPessimisticLock(testAuction.getAuctionId()))
                .willReturn(Optional.empty());

        // when & then (실행 및 검증)
        CustomException exception = assertThrows(CustomException.class,
                () -> bidService.addBid(testUser2.getUserId(), testAuction.getAuctionId(), request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.AUCTION_NOT_FOUND.getCode());
        assertThat(exception.getErrorCode().getMessage()).isEqualTo(ErrorCode.AUCTION_NOT_FOUND.getMessage());
    }

    @Test
    void bidding_invalidAuctionTime() {
        // given (준비)
        LocalDateTime now = LocalDateTime.now();
        Auction testAuction2 = Auction.builder()
                .auctionId(2L)
                .product(testProduct)
                .startPrice(10000L)
                .currentPrice(10000L)
                .startTime(now)
                .endTime(now)
                .status(AuctionStatus.PROCEEDING)
                .build();
        BidRequest request = new BidRequest(15000L);

        given(userRepository.findByIdWithPessimisticLock(testUser2.getUserId()))
                .willReturn(Optional.of(testUser2));

        given(auctionRepository.findByIdWithPessimisticLock(testAuction2.getAuctionId()))
                .willReturn(Optional.of(testAuction2));

        // when & then (실행 및 검증)
        CustomException exception = assertThrows(CustomException.class,
                () -> bidService.addBid(testUser2.getUserId(), testAuction2.getAuctionId(), request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.INVALID_AUCTION_TIME.getCode());
        assertThat(exception.getErrorCode().getMessage()).isEqualTo(ErrorCode.INVALID_AUCTION_TIME.getMessage());
    }

    @Test
    void bidding_selfBidNotAllowed() {
        // given (준비)
        BidRequest request = new BidRequest(15000L);
        given(userRepository.findByIdWithPessimisticLock(testUser1.getUserId()))
                .willReturn(Optional.of(testUser1));

        given(auctionRepository.findByIdWithPessimisticLock(testAuction.getAuctionId()))
                .willReturn(Optional.of(testAuction));

        // when & then (실행 및 검증)
        CustomException exception = assertThrows(CustomException.class,
                () -> bidService.addBid(testUser1.getUserId(), testAuction.getAuctionId(), request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.SELF_BID_NOT_ALLOWED.getCode());
        assertThat(exception.getErrorCode().getMessage()).isEqualTo(ErrorCode.SELF_BID_NOT_ALLOWED.getMessage());
    }

    @Test
    void bidding_startPrice_bidPriceTooLow() {
        // given (준비)
        BidRequest request = new BidRequest(9000L);
        given(userRepository.findByIdWithPessimisticLock(testUser2.getUserId()))
                .willReturn(Optional.of(testUser2));

        given(auctionRepository.findByIdWithPessimisticLock(testAuction.getAuctionId()))
                .willReturn(Optional.of(testAuction));

        given(bidRepository.findTopByStatusAndAuctionOrderByBidIdDesc(BidStatus.ACTIVE, testAuction))
                .willReturn(Optional.empty());

        // when & then (실행 및 검증)
        CustomException exception = assertThrows(CustomException.class,
                () -> bidService.addBid(testUser2.getUserId(), testAuction.getAuctionId(), request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.BID_PRICE_TOO_LOW.getCode());
        assertThat(exception.getErrorCode().getMessage()).isEqualTo(ErrorCode.BID_PRICE_TOO_LOW.getMessage());
    }

    @Test
    void bidding_alreadyHighestBidder() {
        // given (준비)
        BidRequest request = new BidRequest(12000L);
        Bid testBid = Bid.builder()
                .bidId(1L)
                .auction(testAuction)
                .bidder(testUser2)
                .bidPrice(11000L)
                .status(BidStatus.ACTIVE)
                .build();

        given(userRepository.findByIdWithPessimisticLock(testUser2.getUserId()))
                .willReturn(Optional.of(testUser2));

        given(auctionRepository.findByIdWithPessimisticLock(testAuction.getAuctionId()))
                .willReturn(Optional.of(testAuction));

        given(bidRepository.findTopByStatusAndAuctionOrderByBidIdDesc(BidStatus.ACTIVE, testAuction))
                .willReturn(Optional.of(testBid));

        // when & then (실행 및 검증)
        CustomException exception = assertThrows(CustomException.class,
                () -> bidService.addBid(testUser2.getUserId(), testAuction.getAuctionId(), request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.ALREADY_HIGHEST_BIDDER.getCode());
        assertThat(exception.getErrorCode().getMessage()).isEqualTo(ErrorCode.ALREADY_HIGHEST_BIDDER.getMessage());
    }

    @Test
    void bidding_currentPrice_bidPriceTooLow() {
        // given (준비)
        LocalDateTime now = LocalDateTime.now();
        BidRequest request = new BidRequest(12000L);

        User testUser3 = User.builder()
                .userId(3L)
                .email("test3@test.com")
                .password("test!@#")
                .username("test3")
                .nickname("test3")
                .phone("01033333333")
                .role(Role.USER)
                .baseAddress("123")
                .detailAddress("456")
                .point(1000000L)
                .avgRating(0.0)
                .status(UserStatus.NORMAL)
                .build();

        Auction testAuction2 = Auction.builder()
                .auctionId(2L)
                .product(testProduct)
                .startPrice(10000L)
                .currentPrice(15000L)
                .startTime(now)
                .endTime(now.plusDays(5))
                .status(AuctionStatus.PROCEEDING)
                .build();

        Bid testBid = Bid.builder()
                .bidId(1L)
                .auction(testAuction2)
                .bidder(testUser3)
                .bidPrice(15000L)
                .status(BidStatus.ACTIVE)
                .build();

        given(userRepository.findByIdWithPessimisticLock(testUser2.getUserId()))
                .willReturn(Optional.of(testUser2));

        given(auctionRepository.findByIdWithPessimisticLock(testAuction2.getAuctionId()))
                .willReturn(Optional.of(testAuction2));

        given(bidRepository.findTopByStatusAndAuctionOrderByBidIdDesc(BidStatus.ACTIVE, testAuction2))
                .willReturn(Optional.of(testBid));

        // when & then (실행 및 검증)
        CustomException exception = assertThrows(CustomException.class,
                () -> bidService.addBid(testUser2.getUserId(), testAuction2.getAuctionId(), request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.BID_PRICE_TOO_LOW.getCode());
        assertThat(exception.getErrorCode().getMessage()).isEqualTo(ErrorCode.BID_PRICE_TOO_LOW.getMessage());
    }

}
