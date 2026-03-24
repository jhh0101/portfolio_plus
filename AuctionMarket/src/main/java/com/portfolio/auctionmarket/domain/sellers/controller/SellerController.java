package com.portfolio.auctionmarket.domain.sellers.controller;

import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.domain.sellers.dto.RejectReasonResponse;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerApplyRequest;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerRejectRequest;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerResponse;
import com.portfolio.auctionmarket.domain.sellers.service.SellerService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class SellerController {
    private final SellerService sellerService;

    @PostMapping("/seller/apply")
    public ResponseEntity<ApiResponse<SellerResponse>> sellerApply(@AuthenticationPrincipal SecurityUser user,
                                                                   @Valid @RequestBody SellerApplyRequest request) {
        SellerResponse response = sellerService.sellerApply(user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 신청", response));
    }

    @GetMapping("/seller/apply")
    public ResponseEntity<ApiResponse<SellerResponse>> sellerDetails(@AuthenticationPrincipal SecurityUser user) {
        SellerResponse response = sellerService.sellerDetails(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("판매자 신청 내용 조회", response));
    }

    @PatchMapping("/{sellerId}/modify")
    public ResponseEntity<ApiResponse<SellerResponse>> applyModify(@AuthenticationPrincipal SecurityUser user,
                                                                   @PathVariable Long sellerId,
                                                                   @Valid @RequestBody SellerApplyRequest request) {
        SellerResponse response = sellerService.applyModify(user.getUserId(), sellerId, request);
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 수정", response));
    }

    @PatchMapping("/{sellerId}/cancel")
    public ResponseEntity<ApiResponse<SellerResponse>> sellerCancel(@AuthenticationPrincipal SecurityUser user,
                                                                   @PathVariable Long sellerId) {
        SellerResponse response = sellerService.sellerCancel(sellerId, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 취소", response));
    }

    @GetMapping("/reject/reason")
    public ResponseEntity<ApiResponse<RejectReasonResponse>> rejectReason(@AuthenticationPrincipal SecurityUser user) {
        RejectReasonResponse response = sellerService.rejectReason(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("신청 거절 사유 조회", response));
    }


}
