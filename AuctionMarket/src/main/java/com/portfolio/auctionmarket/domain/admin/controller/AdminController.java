package com.portfolio.auctionmarket.domain.admin.controller;

import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.domain.bids.dto.BidHistoryResponse;
import com.portfolio.auctionmarket.domain.bids.dto.BidResponseImpl;
import com.portfolio.auctionmarket.domain.bids.service.BidAdminService;
import com.portfolio.auctionmarket.domain.bids.service.BidService;
import com.portfolio.auctionmarket.domain.orders.dto.OrderResponse;
import com.portfolio.auctionmarket.domain.orders.service.OrderAdminService;
import com.portfolio.auctionmarket.domain.products.dto.ProductAndAuctionResponse;
import com.portfolio.auctionmarket.domain.products.dto.ProductListCondition;
import com.portfolio.auctionmarket.domain.products.service.ProductAdminService;
import com.portfolio.auctionmarket.domain.products.service.ProductService;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerApplyListResponse;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerRejectRequest;
import com.portfolio.auctionmarket.domain.sellers.dto.SellerResponse;
import com.portfolio.auctionmarket.domain.sellers.service.SellerAdminService;
import com.portfolio.auctionmarket.domain.sellers.service.SellerService;
import com.portfolio.auctionmarket.domain.user.dto.*;
import com.portfolio.auctionmarket.domain.user.service.UserAdminService;
import com.portfolio.auctionmarket.domain.user.service.UserService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserAdminService userAdminService;
    private final UserService userService;
    private final BidAdminService bidAdminService;
    private final ProductAdminService productAdminService;
    private final OrderAdminService orderAdminService;
    private final SellerAdminService sellerAdminService;

    @PostMapping("/{userId}/suspend")
    public ResponseEntity<ApiResponse<UserDeleteResponse>> suspend(@PathVariable Long userId, @RequestBody UserSuspensionRequest request) {
        UserDeleteResponse response = userAdminService.suspend(userId, request);
        return ResponseEntity.ok(ApiResponse.success("회원 정지", response));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> userList(UserListCondition condition, Pageable pageable) {
        Page<UserResponse> responses = userAdminService.userList(condition, pageable);
        return ResponseEntity.ok(ApiResponse.success("회원 리스트 조회", responses));
    }

    @GetMapping("/suspension-status/{userId}")
    public ResponseEntity<ApiResponse<WithdrawalStatusResponse>> suspensionStatus(@PathVariable Long userId) {
        WithdrawalStatusResponse response = userService.withdrawalStatus(userId);
        return ResponseEntity.ok(ApiResponse.success("정지 회원의 상태 조회", response));
    }

    @GetMapping("/suspension-reason/{userId}")
    public ResponseEntity<ApiResponse<UserSuspendReasonResponse>> suspensionReason(@PathVariable Long userId) {
        UserSuspendReasonResponse response = userAdminService.suspendReason(userId);
        return ResponseEntity.ok(ApiResponse.success("정지 회원의 정지 사유 조회", response));
    }

    @GetMapping("/{userId}/bid")
    public ResponseEntity<ApiResponse<Slice<BidHistoryResponse>>> findBidHistory(@PathVariable Long userId,
                                                                                 @PageableDefault(size = 10) Pageable pageable){
        Slice<BidHistoryResponse> responses = bidAdminService.findBidHistory(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("사용자의 입찰 상품 리스트 출력", responses));
    }

    @GetMapping("/{auctionId}/bid-list/{userId}")
    public ResponseEntity<ApiResponse<Slice<BidResponseImpl>>> findUserBidList(@PathVariable Long userId,
                                                                               @PathVariable Long auctionId,
                                                                               @PageableDefault(size = 10) Pageable pageable){
        Slice<BidResponseImpl> responses = bidAdminService.findUserBidList(userId, auctionId, pageable);
        return ResponseEntity.ok(ApiResponse.success("사용자의 입찰 리스트 출력", responses));
    }

    @GetMapping("/{userId}/product")
    public ResponseEntity<ApiResponse<Slice<ProductAndAuctionResponse>>> myProductList(@PathVariable Long userId,
                                                                                      ProductListCondition condition,
                                                                                      @PageableDefault(size = 5) Pageable pageable) {
        Slice<ProductAndAuctionResponse> responses = productAdminService.userProductList(userId, condition, pageable);
        return ResponseEntity.ok(ApiResponse.success("사용자의 상품 리스트 조회", responses));
    }

    @GetMapping("/{userId}/order")
    public ResponseEntity<ApiResponse<Slice<OrderResponse>>> findOrder(@PathVariable Long userId,
                                                                      @PageableDefault(size = 10, sort = "orderId", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<OrderResponse> responses = orderAdminService.findOrder(userId, pageable);
        log.info("[Order] 사용자 {}의 낙찰 리스트 조회 요청 (page: {})", userId, pageable.getPageNumber());
        return ResponseEntity.ok(ApiResponse.success("낙찰 리스트 조회", responses));
    }

    // 판매자 신청 관련
    @GetMapping("/apply/list")
    public ResponseEntity<ApiResponse<Page<SellerApplyListResponse>>> sellerList(@PageableDefault(size = 10, sort = "sellerId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SellerApplyListResponse> response = sellerAdminService.sellerList(pageable);
        return ResponseEntity.ok(ApiResponse.success("판매 신청자 리스트 조회", response));
    }

    @PatchMapping("/{sellerId}/approve")
    public ResponseEntity<ApiResponse<SellerResponse>> sellerApprove(@PathVariable Long sellerId) {
        SellerResponse response = sellerAdminService.approveSeller(sellerId);
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 승인", response));
    }

    @PatchMapping("/{sellerId}/reject")
    public ResponseEntity<ApiResponse<SellerResponse>> sellerReject(@PathVariable Long sellerId,
                                                                    @Valid @RequestBody SellerRejectRequest request) {
        SellerResponse response = sellerAdminService.rejectSeller(sellerId, request);
        return ResponseEntity.ok(ApiResponse.success("판매자 등록 거절", response));
    }

    @GetMapping("/seller/{sellerId}/apply")
    public ResponseEntity<ApiResponse<SellerResponse>> sellerDetails(@PathVariable Long sellerId) {
        SellerResponse response = sellerAdminService.sellerDetails(sellerId);
        return ResponseEntity.ok(ApiResponse.success("판매자 신청 내용 조회", response));
    }

}
