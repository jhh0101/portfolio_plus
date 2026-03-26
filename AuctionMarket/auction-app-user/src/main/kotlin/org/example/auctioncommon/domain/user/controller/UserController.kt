package org.example.auctioncommon.domain.user.controller

import jakarta.validation.Valid
import org.example.auctioncommon.auth.dto.SecurityUser
import org.example.auctioncommon.domain.user.dto.UserDeleteResponse
import org.example.auctioncommon.domain.user.dto.UserNewPasswordRequest
import org.example.auctioncommon.domain.user.dto.UserProfileResponse
import org.example.auctioncommon.domain.user.dto.UserResponse
import org.example.auctioncommon.domain.user.dto.UserSingupRequest
import org.example.auctioncommon.domain.user.dto.UserUpdateRequest
import org.example.auctioncommon.domain.user.dto.UserWithdrawnRequest
import org.example.auctioncommon.domain.user.dto.WithdrawalStatusResponse
import org.example.auctioncommon.domain.user.service.UserService
import org.example.auctioncommon.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/signup")
    fun signup(@RequestBody request: @Valid UserSingupRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val userResponse: UserResponse = userService.signup(request)
        return ResponseEntity
            .status(org.springframework.http.HttpStatus.CREATED)
            .body(ApiResponse.success("회원가입 성공", userResponse))
    }

    @PostMapping("/withdrawn")
    fun withdrawn(
        @AuthenticationPrincipal user: SecurityUser,
        @RequestBody request: UserWithdrawnRequest
    ): ResponseEntity<ApiResponse<UserDeleteResponse>> {
        val response: UserDeleteResponse = userService.withdrawn(user.userId, request)
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴", response))
    }

    @GetMapping("/my/profile")
    fun profile(@AuthenticationPrincipal user: SecurityUser): ResponseEntity<ApiResponse<UserProfileResponse>> {
        val response: UserProfileResponse = userService.profile(user.userId)
        return ResponseEntity.ok(ApiResponse.success("프로필 조회", response))
    }

    @PatchMapping("/my/profile/edit")
    fun updateUser(
        @AuthenticationPrincipal user: SecurityUser,
        @RequestBody request: @Valid UserUpdateRequest
    ): ResponseEntity<ApiResponse<UserProfileResponse>> {
        val response: UserProfileResponse = userService.updateUser(user.userId, request)
        return ResponseEntity.ok(ApiResponse.success("회원 수정", response))
    }

    @PatchMapping("/new-password")
    fun updatePassword(
        @AuthenticationPrincipal user: SecurityUser,
        @RequestBody request: @Valid UserNewPasswordRequest
    ): ResponseEntity<ApiResponse<Void>> {
        userService.updatePassword(user.userId, request)
        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경 성공", null))
    }

    @GetMapping("/withdrawal-status")
    fun withdrawalStatus(@AuthenticationPrincipal user: SecurityUser): ResponseEntity<ApiResponse<WithdrawalStatusResponse>> {
        val response: WithdrawalStatusResponse = userService.withdrawalStatus(user.userId)
        return ResponseEntity.ok(ApiResponse.success("탈퇴 조건 조회", response))
    }
}
