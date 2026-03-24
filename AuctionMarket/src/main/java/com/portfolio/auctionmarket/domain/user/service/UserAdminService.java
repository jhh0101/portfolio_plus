package com.portfolio.auctionmarket.domain.user.service;

import com.portfolio.auctionmarket.auth.service.RefreshTokenService;
import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import com.portfolio.auctionmarket.domain.bids.entity.BidStatus;
import com.portfolio.auctionmarket.domain.bids.repository.BidRepository;
import com.portfolio.auctionmarket.domain.bids.service.BidService;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import com.portfolio.auctionmarket.domain.products.repository.ProductRepository;
import com.portfolio.auctionmarket.domain.user.dto.*;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserQueryRepository;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import com.portfolio.auctionmarket.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminService {
    private static final Set<Long> PROTECTED_USER_IDS = Set.of(1L, 2L, 3L, 4L);

    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final BidService bidService;
    private final ProductRepository productRepository;
    private final UserQueryRepository userQueryRepository;
    private final RefreshTokenService refreshTokenService;
    private final S3Service s3Service;
    private final RedissonClient redissonClient;

    @Transactional
    public UserDeleteResponse suspend(Long userId, UserSuspensionRequest request) {
        validateUserProtection(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<Product> productList = productRepository.findAllBySeller_UserId(userId);
        List<Bid> bidList = bidRepository.findTopBidsPerAuctionBySellerId(userId);

        List<String> allImageUrls = new ArrayList<>();
        List<Long> auctionIds = new ArrayList<>();

        List<Bid> userBids = bidRepository.findLatestBidsByUserId(userId);

        for (Bid userBid : userBids) {
            Auction auction = userBid.getAuction();
            if (userBid.getAuction().getStatus() == AuctionStatus.PROCEEDING) {

                Bid currentTopBid = bidRepository.findTopByStatusAndAuctionOrderByBidPriceDesc(BidStatus.ACTIVE, auction)
                        .orElse(null);

                if (currentTopBid != null && currentTopBid.getBidder().getUserId().equals(userId)) {

                    bidService.cancelBid(user.getUserId(), userBid.getBidId(), auction.getAuctionId());

                } else {

                    log.info("사용자가 상위 입찰자가 아니므로 취소 패스 - AuctionId: {}", auction.getAuctionId());

                }
            }
        }

        for (Bid bid : bidList) {
            User users = bid.getBidder();
            users.addPoint(bid.getBidPrice());
        }

        for (Product product : productList) {
            for (ProductImage img : product.getImage()) {
                allImageUrls.add(img.getImageUrl());
            }
            auctionIds.add(product.getAuction().getAuctionId());
        }


        productRepository.deleteAllBySeller_UserId(userId);
        refreshTokenService.deleteRefreshToken(userId);
        user.suspend(userId, request.getSuspensionReason());

        RScoredSortedSet<Long> closingQueue = redissonClient.getScoredSortedSet("auction:closing");
        closingQueue.removeAll(auctionIds);

        try {
            s3Service.deleteFiles(allImageUrls);
        } catch (Exception e) {
            log.error("S3 이미지 삭제 중 오류 발생 (DB는 롤백됨): {}", e.getMessage());
        }
        return UserDeleteResponse.from(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> userList(UserListCondition condition, Pageable pageable) {
        Page<User> users = userQueryRepository.userList(condition, pageable);
        return users.map(UserResponse::from);
    }

    @Transactional(readOnly = true)
    public UserSuspendReasonResponse suspendReason(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return UserSuspendReasonResponse.from(user);
    }

    private void validateUserProtection(Long userId) {
        if (PROTECTED_USER_IDS.contains(userId)) {
            throw new CustomException(ErrorCode.PROTECT_DEFAULT_USERS);
        }
    }
}
