package com.portfolio.auctionmarket.domain.sellers.service;

import com.portfolio.auctionmarket.domain.sellers.dto.SellerApplyListResponse;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerRejectRequest;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerResponse;
import com.portfolio.auctionmarket.domain.sellers.entity.Seller;
import com.portfolio.auctionmarket.domain.sellers.entity.SellerStatus;
import com.portfolio.auctionmarket.domain.sellers.repository.SellerRepository;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SellerAdminService {
    private static final Set<Long> PROTECTED_USER_IDS = Set.of(1L, 2L, 3L, 4L);

    private final SellerRepository sellerRepository;

    @Transactional(readOnly = true)
    public Page<SellerApplyListResponse> sellerList(Pageable pageable) {
        Page<Seller> sellers = sellerRepository.findAllByStatus(SellerStatus.PENDING, pageable);
        return sellers.map(SellerApplyListResponse::from);
    }

    @Transactional
    public SellerResponse approveSeller(Long sellerId) {
        validateUserProtection(sellerId);
        
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_FOUND, "판매자를 찾을 수 없습니다."));

        if (seller.getStatus() != SellerStatus.PENDING) {
            throw new CustomException(ErrorCode.BAD_REQUEST,
                    "현재 상태(" + seller.getStatus() + ")에서는 승인할 수 없습니다.");
        }

        seller.approveSeller();

        return SellerResponse.from(seller);
    }

    @Transactional
    public SellerResponse rejectSeller(Long sellerId, SellerRejectRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_FOUND, "판매자를 찾을 수 없습니다."));

        if (seller.getStatus() != SellerStatus.PENDING) {
            throw new CustomException(ErrorCode.BAD_REQUEST,
                    "현재 상태(" + seller.getStatus() + ")에서는 거절할 수 없습니다.");
        }

        seller.rejectSeller(request.getRejectReason());

        return SellerResponse.from(seller);
    }

    @Transactional(readOnly = true)
    public SellerResponse sellerDetails(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return SellerResponse.from(seller);
    }

    private void validateUserProtection(Long userId) {
        if (PROTECTED_USER_IDS.contains(userId)) {
            throw new CustomException(ErrorCode.PROTECT_DEFAULT_USERS);
        }
    }
}
