package com.portfolio.auctionmarket.domain.sellers.service;

import com.portfolio.auctionmarket.domain.sellers.dto.RejectReasonResponse;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerApplyRequest;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerRejectRequest;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerResponse;
import com.portfolio.auctionmarket.domain.sellers.entity.Seller;
import com.portfolio.auctionmarket.domain.sellers.entity.SellerStatus;
import com.portfolio.auctionmarket.domain.sellers.repository.SellerRepository;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SellerService {
    private static final Set<Long> PROTECTED_USER_IDS = Set.of(1L, 2L, 3L, 4L);

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    @Transactional
    public SellerResponse sellerApply(Long userId, SellerApplyRequest request) {
        validateUserProtection(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Optional<Seller> optionalSeller = sellerRepository.findByUser_UserId(userId);
        Seller seller;

        if (optionalSeller.isPresent()) {
            seller = optionalSeller.get();
            if (seller.getStatus() != SellerStatus.REJECTED && seller.getStatus() != SellerStatus.CANCELED) {
                throw new CustomException(ErrorCode.DUPLICATE_SELLER, "이미 신청 중...");
            }
            seller.updateApply(user, request);
        } else {
            seller = Seller.builder()
                    .user(user)
                    .storeName(request.getStoreName())
                    .bankName(request.getBankName())
                    .accountNumber(request.getAccountNumber())
                    .accountHolder(request.getAccountHolder())
                    .status(SellerStatus.PENDING)
                    .build();
        }

        sellerRepository.save(seller);

        return SellerResponse.from(seller);
    }

    @Transactional(readOnly = true)
    public SellerResponse sellerDetails(Long userId) {
        Seller seller = sellerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return SellerResponse.from(seller);
    }

    @Transactional
    public SellerResponse applyModify(Long userId, Long sellerId, SellerApplyRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_FOUND));

        if (!userId.equals(seller.getUser().getUserId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "사용자 정보가 일치하지 않습니다.");
        }

        seller.updateApply(seller.getUser(), request);

        return SellerResponse.from(seller);
    }

    @Transactional
    public SellerResponse sellerCancel(Long sellerId, Long userId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_FOUND));

        if (!userId.equals(seller.getUser().getUserId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "사용자 정보가 일치하지 않습니다.");
        }

        if (seller.getStatus() == SellerStatus.CANCELED || seller.getStatus() == SellerStatus.REJECTED) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "취소할 수 없는 상태입니다. (이미 취소 또는 반려됨)");
        }

        seller.cancelSeller();

        return SellerResponse.from(seller);
    }

    @Transactional(readOnly = true)
    public RejectReasonResponse rejectReason(Long userId) {
        Seller seller = sellerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_FOUND));

        return RejectReasonResponse.from(seller);
    }

    private void validateUserProtection(Long userId) {
        if (PROTECTED_USER_IDS.contains(userId)) {
            throw new CustomException(ErrorCode.PROTECT_DEFAULT_USERS);
        }
    }
}
