package com.portfolio.auctionmarket.domain.ratings.controller;

import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.domain.ratings.dto.RatingDeleteResponse;
import com.portfolio.auctionmarket.domain.ratings.dto.RatingRequest;
import com.portfolio.auctionmarket.domain.ratings.dto.RatingResponse;
import com.portfolio.auctionmarket.domain.ratings.service.RatingService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<RatingResponse>> createRating(@AuthenticationPrincipal SecurityUser user,
                                                                    @PathVariable Long orderId,
                                                                    @RequestBody RatingRequest request) {
        RatingResponse response = ratingService.createRating(user.getUserId(), orderId, request);
        log.info("평가 등록 시도 - 사용자ID: {}, 점수: {}, 코멘트: {}", user.getUserId(), request.getScore(), request.getComment());
        return ResponseEntity.ok(ApiResponse.success("판매자 평가 등록", response));
    }

    @GetMapping("/{orderId}/one")
    public ResponseEntity<ApiResponse<RatingResponse>> findRating(@PathVariable Long orderId) {
        RatingResponse response = ratingService.findRating(orderId);
        return ResponseEntity.ok(ApiResponse.success("판매자 평가 조회", response));
    }

    @PatchMapping("/{orderId}/update/{ratingId}")
    public ResponseEntity<ApiResponse<RatingResponse>> updateRating(@AuthenticationPrincipal SecurityUser user,
                                                                    @PathVariable Long orderId,
                                                                    @PathVariable Long ratingId,
                                                                    @RequestBody RatingRequest request) {
        RatingResponse response = ratingService.updateRating(user.getUserId(), orderId, ratingId, request);
        log.info("평가 수정 시도 - 사용자ID: {}, 점수: {}, 코멘트: {}", user.getUserId(), request.getScore(), request.getComment());
        return ResponseEntity.ok(ApiResponse.success("판매자 평가 수정", response));
    }

    @DeleteMapping("/delete/{ratingId}")
    public ResponseEntity<ApiResponse<RatingDeleteResponse>> deleteRating(@AuthenticationPrincipal SecurityUser user,
                                                                          @PathVariable Long ratingId) {
        RatingDeleteResponse response = ratingService.deleteRating(user.getUserId(), ratingId);
        log.info("평가 수정 시도 - 사용자ID: {}, 평가ID: {}", user.getUserId(), ratingId);
        return ResponseEntity.ok(ApiResponse.success("판매자 평가 삭제", response));
    }

    @GetMapping("/{toUserId}/list")
    public ResponseEntity<ApiResponse<Page<RatingResponse>>> findRatingList(@PathVariable Long toUserId,
                                                                            @PageableDefault(size = 10, sort = "order.orderId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<RatingResponse> responses = ratingService.findRatingList(toUserId, pageable);
        return ResponseEntity.ok(ApiResponse.success("판매자 평가 리스트 조회", responses));
    }
}
