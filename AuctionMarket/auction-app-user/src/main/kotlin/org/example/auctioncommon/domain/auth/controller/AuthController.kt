package org.example.auctioncommon.domain.auth.controller

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.example.auctioncommon.domain.auth.dto.LoginRequest
import org.example.auctioncommon.domain.auth.dto.SecurityUser
import org.example.auctioncommon.domain.auth.dto.TokenResponse
import org.example.auctioncommon.domain.auth.service.AuthService
import org.example.auctioncommon.global.response.ApiResponse
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {


    @PostMapping("/login")
    fun login(
        @RequestBody request: @Valid LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<String>> {
        val tokenResponse: TokenResponse = authService.login(request)
        val responseCookie: ResponseCookie = ResponseCookie.from("refreshToken", tokenResponse.refreshToken)
            .path("/")
            .secure(false)
            .httpOnly(true)
            .sameSite("Lax")
            .maxAge((7 * 24 * 60 * 60).toLong())
            .build()

        response.setHeader("Set-Cookie", responseCookie.toString())
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", tokenResponse.accessToken))
    }

    @PostMapping("/logout")
    fun logout(@AuthenticationPrincipal user: SecurityUser): ResponseEntity<ApiResponse<String>> {
        authService.logout(user.userId)
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공"))
    }

    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(value = "refreshToken", required = false) refreshToken: String,
        response: HttpServletResponse
    ): ApiResponse<String> {
        val tokenResponse: TokenResponse = authService.refreshAccessToken(refreshToken)
        val responseCookie: ResponseCookie = ResponseCookie.from("refreshToken", tokenResponse.refreshToken)
            .path("/")
            .secure(false) // https 환경에서만 쿠키가 발동합니다.
            .sameSite("Lax") // 동일 사이트과 크로스 사이트에 모두 쿠키 전송이 가능합니다
            .httpOnly(true) // 브라우저에서 쿠키에 접근할 수 없도록 제한
            .maxAge((7 * 24 * 60 * 60).toLong())
            .build()

        response.setHeader("Set-Cookie", responseCookie.toString())
        return ApiResponse.success("토큰 갱신 성공", tokenResponse.accessToken)
    }
}
