package com.portfolio.auctionmarket.domain.toss.controller;

import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.domain.toss.dto.TossPaymentConfirmRequest;
import com.portfolio.auctionmarket.domain.toss.dto.TossPaymentConfirmResponse;
import com.portfolio.auctionmarket.domain.toss.service.TossPaymentService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class TossPaymentConfirmController {
    private final TossPaymentService tossPaymentService;

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<TossPaymentConfirmResponse>> confirm(@AuthenticationPrincipal SecurityUser user,
                                                                           @RequestBody TossPaymentConfirmRequest request) {
        TossPaymentConfirmResponse response = tossPaymentService.confirm(request, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("토스 결제 성공", response));
    }

}
