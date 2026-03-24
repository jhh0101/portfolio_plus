package com.portfolio.auctionmarket.domain.orders.controller;

import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.domain.orders.dto.OrderResponse;
import com.portfolio.auctionmarket.domain.orders.service.OrderService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> findOrder(@AuthenticationPrincipal SecurityUser user,
                                                                      @PageableDefault(size = 10, sort = "orderId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderResponse> responses = orderService.findOrder(user.getUserId(), pageable);
        log.info("[Order] 사용자 {}의 낙찰 리스트 조회 요청 (page: {})", user.getUserId(), pageable.getPageNumber());
        return ResponseEntity.ok(ApiResponse.success("낙찰 리스트 조회", responses));
    }

    @GetMapping("/{auctionId}/auction")
    public ResponseEntity<ApiResponse<OrderResponse>> auctionOrder(@PathVariable Long auctionId) {
        OrderResponse response = orderService.auctionOrder(auctionId);
        log.info("[Auction] 옥션 {}의 낙찰 조회 요청", auctionId);
        return ResponseEntity.ok(ApiResponse.success("낙찰자 조회", response));
    }

}
