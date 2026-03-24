package com.portfolio.auctionmarket.domain.user.controller;

import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.domain.user.dto.*;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.service.UserService;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody UserSingupRequest request){
        UserResponse userResponse = userService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입 성공", userResponse));
    }

    @PostMapping("/withdrawn")
    public ResponseEntity<ApiResponse<UserDeleteResponse>> withdrawn(@AuthenticationPrincipal SecurityUser user, @RequestBody UserWithdrawnRequest request) {
        UserDeleteResponse response = userService.withdrawn(user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴", response));
    }

    @GetMapping("/my/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> profile(@AuthenticationPrincipal SecurityUser user) {
        UserProfileResponse response = userService.profile(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("프로필 조회", response));
    }

    @PatchMapping("/my/profile/edit")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUser(@AuthenticationPrincipal SecurityUser user,@Valid @RequestBody UserUpdateRequest request) {
        UserProfileResponse response = userService.updateUser(user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("회원 수정", response));
    }

    @PatchMapping("/new-password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@AuthenticationPrincipal SecurityUser user, @Valid @RequestBody UserNewPasswordRequest request) {
        userService.updatePassword(user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경 성공", null));
    }

    @GetMapping("/withdrawal-status")
    public ResponseEntity<ApiResponse<WithdrawalStatusResponse>> withdrawalStatus(@AuthenticationPrincipal SecurityUser user) {
        WithdrawalStatusResponse response = userService.withdrawalStatus(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("탈퇴 조건 조회", response));
    }

}
