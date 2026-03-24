package com.portfolio.auctionmarket.domain.user.service;

import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.auth.service.RefreshTokenService;
import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.auctions.repository.AuctionRepository;
import com.portfolio.auctionmarket.domain.bids.entity.Bid;
import com.portfolio.auctionmarket.domain.bids.entity.BidStatus;
import com.portfolio.auctionmarket.domain.bids.repository.BidRepository;
import com.portfolio.auctionmarket.domain.bids.service.BidService;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import com.portfolio.auctionmarket.domain.products.repository.ProductRepository;
import com.portfolio.auctionmarket.domain.user.dto.*;
import com.portfolio.auctionmarket.domain.user.dto.UserResponse;
import com.portfolio.auctionmarket.domain.user.dto.UserSingupRequest;
import com.portfolio.auctionmarket.domain.user.dto.UserSuspensionRequest;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.sellers.entity.SellerStatus;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.entity.UserStatus;
import com.portfolio.auctionmarket.domain.user.repository.UserQueryRepository;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import com.portfolio.auctionmarket.global.s3.service.S3Service;
import com.portfolio.auctionmarket.global.util.MaskingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.portfolio.auctionmarket.domain.bids.entity.QBid.bid;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final Set<Long> PROTECTED_USER_IDS = Set.of(1L, 2L, 3L, 4L);

    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final ProductRepository productRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(UserSingupRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            if (UserStatus.SUSPENDED.equals(user.getStatus())) {
                throw new CustomException(ErrorCode.SUSPENDED_USER, "정지된 사용자입니다.");
            }
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL, "이미 사용 중인 이메일입니다.");
        });
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME, "이미 사용 중인 닉네임입니다.");
        }
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .nickname(request.getNickname())
                .baseAddress(request.getBaseAddress())
                .detailAddress(request.getDetailAddress())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .point(0L)
                .avgRating(0.0)
                .status(UserStatus.NORMAL)
                .role(Role.USER)
                .build();

        User saveUser = userRepository.save(user);
        log.info("User created: userId={}, email={}, nickname={}", saveUser.getUserId(), saveUser.getEmail(), saveUser.getNickname());

        return UserResponse.from(saveUser);
    }

    @Transactional
    public UserDeleteResponse withdrawn(Long userId, UserWithdrawnRequest request) {
        validateUserProtection(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH, "비밀번호가 일치하지 않습니다.");
        }

        Long bidCount = bidRepository.bidCount(userId);
        Long productCount = productRepository.productCount(userId);

        if (bidCount + productCount > 0) {
            throw new CustomException(ErrorCode.CANNOT_WITHDRAW_WHILE_TRADING, "현재 진행 중인 거래가 있습니다.");
        }

        refreshTokenService.deleteRefreshToken(userId);

        String formatPhone = MaskingUtil.formatPhone(user.getPhone());
        String maskEmail = MaskingUtil.maskEmail(user.getEmail());
        String maskUsername = MaskingUtil.maskUsername(user.getUsername());
        String maskPhone = MaskingUtil.maskPhone(formatPhone);

        user.withdraw(maskEmail, maskUsername, maskPhone, userId);

        return UserDeleteResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse profile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!user.getNickname().equals(request.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new CustomException(ErrorCode.DUPLICATE_NICKNAME, "이미 사용 중인 닉네임입니다.");
            }
        }

        user.updateUser(request);

        return UserProfileResponse.from(user);
    }

    @Transactional
    public void updatePassword(Long userId, UserNewPasswordRequest request) {
        validateUserProtection(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH, "현재 비밀번호가 일치하지 않습니다.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH, "변경할 비밀번호와 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional(readOnly = true)
    public WithdrawalStatusResponse withdrawalStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Long bidCount = bidRepository.bidCount(userId);

        Long productCount = productRepository.productCount(userId);

        return WithdrawalStatusResponse.from(user, bidCount, productCount);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!user.getStatus().equals(UserStatus.NORMAL)) {
            throw new CustomException(ErrorCode.SUSPENDED_USER, "정지된 사용자입니다.");
        }

        return new SecurityUser(user);
    }

    private void validateUserProtection(Long userId) {
        if (PROTECTED_USER_IDS.contains(userId)) {
            throw new CustomException(ErrorCode.PROTECT_DEFAULT_USERS);
        }
    }
}
